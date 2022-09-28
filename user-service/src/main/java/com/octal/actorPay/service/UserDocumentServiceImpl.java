package com.octal.actorPay.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.EkycStatus;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.EkycRequest;
import com.octal.actorPay.dto.request.EkycUpdateRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserDocError;
import com.octal.actorPay.entities.UserDocument;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.repositories.UserDocumentRepository;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.transformer.UserTransformer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

@Service
public class UserDocumentServiceImpl implements UserDocumentService {

    @Autowired
    private EkycService ekycService;

    @Autowired
    private UserRepository userRepository;

    @Value("${kyc.client-id}")
    private String kycClientId;

    @Value("${kyc.client-secret}")
    private String kycClientSecret;

    @Value("${kyc.base-endpoint}")
    private String kycBaseEndpoint;

    @Value("${kyc.verification-endpoint}")
    private String kycVerificationEndpoint;

    @Autowired
    private SpecificationFactory<UserDocument> userSpecificationFactory;

    @Autowired
    private UserDocumentRepository userDocumentRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public EkycUserResponse verify(MultipartFile frontPart, MultipartFile backPart, User user) throws Exception {
        List<UserDocError> userDocErrors = new ArrayList<>();
        UserDocument userDocument = new UserDocument();
        EkycUserResponse ekycUserResponse = new EkycUserResponse();
        docValidation(frontPart, backPart);
        String userFullName = user.getFirstName();
//        String userFullName = user.getFirstName() + " " + user.getLastName();
        user.setEkycStatus(EkycStatus.IN_PROGRESS);
        ekycService.loadEkycClientCredentials(kycBaseEndpoint, kycVerificationEndpoint, kycClientId, kycClientSecret);
        EkycResponse ekycResponse = ekycService.verify(frontPart, backPart);
        String names[] = ekycResponse.getName().split(" ");
        if (ekycResponse == null)
            throw new ActorPayException("Unable to verify your Document");

        if (ekycResponse.getIdType().equalsIgnoreCase(CommonConstant.EKYC_AADHAR_DOC_TYPE)) {
            Optional<UserDocument> userDocumentOpt = userDocumentRepository
                    .findByUserAndDocType(user.getId(), CommonConstant.EKYC_AADHAR_DOC_TYPE);
            if (userDocumentOpt.isPresent()) {
                if (!userDocumentOpt.get().getEkycStatus().equals(EkycStatus.DECLINED))
                    throw new ActorPayException("The Document already uploaded - Current Status " + userDocumentOpt.get().getEkycStatus());
                else {
                    userDocumentRepository.delete(userDocumentOpt.get());
                    userDocumentRepository.flush();
                }
            }
//            Long aadhaarNoCount = userRepository.findAadhaarDuplicateCount(user.getEmail(), ekycResponse.getIdNo());
//            if (aadhaarNoCount != null && aadhaarNoCount > 0) {
//                UserDocError userDocError = new UserDocError();
//                userDocError.setErrorMessage("The AADHAAR No on Card is already used by another User");
//                userDocErrors.add(userDocError);
//            }
//            Long aadhaarNameCount = userRepository.findDuplicateName(user.getEmail(), names[0], names[1]);
//            if (aadhaarNameCount != null && aadhaarNameCount > 0) {
//                UserDocError userDocError = new UserDocError();
//                userDocError.setErrorMessage("The AADHAAR Name on Card is already used by another User");
//                userDocErrors.add(userDocError);
//            }
            if (StringUtils.isBlank(user.getAadharNumber()))
                throw new ActorPayException("User Aadhaar Number is not exist in the System");

            String userAaadhaarNumber = user.getAadharNumber().replaceAll("\\s", "");
            if (!ekycResponse.getIdNo().equalsIgnoreCase(userAaadhaarNumber)) {
                UserDocError userDocError = new UserDocError();
                userDocError.setErrorMessage("The AADHAAR No on Card is not with our Record");
                userDocErrors.add(userDocError);
            }
            if (!ekycResponse.getName().toLowerCase().trim().contains(userFullName.toLowerCase().trim())) {
                UserDocError userDocError = new UserDocError();
                userDocError.setErrorMessage("Name on Aadhaar is not Matching with Your name in our Record");
                userDocErrors.add(userDocError);
            }
//            if (!userFullName.equalsIgnoreCase(ekycResponse.getName())) {
//                UserDocError userDocError = new UserDocError();
//                userDocError.setErrorMessage("Name on Aadhaar is not Matching with Your name in our Record");
//                userDocErrors.add(userDocError);
//            }
            userDocument.setUser(user);
            userDocument.setActive(Boolean.TRUE);
            ObjectMapper objectMapper = new ObjectMapper();
            String responseJson = objectMapper.writeValueAsString(ekycResponse);
            userDocument.setDocumentData(responseJson);
            userDocument.setDocType(ekycResponse.getIdType());
            userDocument.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            userDocument.setIdNo(ekycResponse.getIdNo());
            if (!userDocErrors.isEmpty()) {
                userDocument.setEkycStatus(EkycStatus.IN_PROGRESS);
            }
        }

        if (ekycResponse.getIdType().equalsIgnoreCase(CommonConstant.EKYC_PAN_DOC_TYPE)) {
            Optional<UserDocument> userDocumentOpt = userDocumentRepository.findByUserAndDocType(user.getId(), CommonConstant.EKYC_PAN_DOC_TYPE);
            if (userDocumentOpt.isPresent()) {
                if (!userDocumentOpt.get().getEkycStatus().equals(EkycStatus.DECLINED))
                    throw new ActorPayException("The Document already uploaded - Current Status " + userDocumentOpt.get().getEkycStatus());
                else {
                    userDocumentRepository.delete(userDocumentOpt.get());
                    userDocumentRepository.flush();
                }
            }
//            Long panNoCount = userRepository.findPanDuplicateCount(user.getEmail(), ekycResponse.getIdNo());
//            if (panNoCount != null && panNoCount > 0) {
//                UserDocError userDocError = new UserDocError();
//                userDocError.setErrorMessage("The PAN No on Card is already used by another User");
//                userDocErrors.add(userDocError);
//            }
//            Long panNameCount = userRepository.findDuplicateName(user.getEmail(), names[0], names[1]);
//            if (panNameCount != null && panNameCount > 0) {
//                UserDocError userDocError = new UserDocError();
//                userDocError.setErrorMessage("The PAN Name on Card is already used by another User");
//                userDocErrors.add(userDocError);
//            }
            if (!ekycResponse.getIdNo().equalsIgnoreCase(user.getPanNumber())) {
                UserDocError userDocError = new UserDocError();
                userDocError.setErrorMessage("The PAN No on Card is not with our Record");
                userDocErrors.add(userDocError);
            }
            if (!ekycResponse.getName().toLowerCase().trim().contains(userFullName.toLowerCase().trim())) {
                UserDocError userDocError = new UserDocError();
                userDocError.setErrorMessage("Name on PAN is not Matching with Your name in our Record");
                userDocErrors.add(userDocError);
            }

//            if (!userFullName.equalsIgnoreCase(ekycResponse.getName())) {
//                UserDocError userDocError = new UserDocError();
//                userDocError.setErrorMessage("Name on PAN is not Matching with Your name in our Record");
//                userDocErrors.add(userDocError);
//            }
            userDocument.setUser(user);
            userDocument.setActive(Boolean.TRUE);
            ObjectMapper objectMapper = new ObjectMapper();
            String responseJson = objectMapper.writeValueAsString(ekycResponse);
            userDocument.setDocumentData(responseJson);
            userDocument.setDocType(ekycResponse.getIdType());
            userDocument.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            userDocument.setIdNo(ekycResponse.getIdNo());
            if (!userDocErrors.isEmpty()) {
                userDocument.setEkycStatus(EkycStatus.DECLINED);
            }
        }
        List<UserDocError> userDocErrorList = buildUserDocError(userDocument, userDocErrors);
        if (!userDocErrorList.isEmpty()) {
            userDocument.setDocErrorList(userDocErrorList);
            userDocument.setEkycStatus(EkycStatus.DECLINED);
        } else {
            userDocument.setEkycStatus(EkycStatus.COMPLETED);
        }
        userDocumentRepository.saveAndFlush(userDocument);
        if (ekycResponse.getIdType().equalsIgnoreCase(CommonConstant.EKYC_AADHAR_DOC_TYPE)) {
            UserDocument panDoc = userDocumentRepository.findByUserAndDocType(user.getId(), CommonConstant.EKYC_PAN_DOC_TYPE).orElse(null);
            if (panDoc != null) {
                if (panDoc.getEkycStatus().equals(EkycStatus.COMPLETED) &&
                        userDocument.getEkycStatus().equals(EkycStatus.COMPLETED)) {
                    user.setEkycStatus(EkycStatus.COMPLETED);
                    userRepository.save(user);
                    ekycUserResponse.setMessage(String.format("eKYC Verification Status for PAN"));
                    ekycUserResponse.setGender(ekycResponse.getGender());
                    ekycUserResponse.setIdNo(ekycResponse.getIdNo());
                    UserDocumentDTO panDocDTO = buildUserDocumentDTO(panDoc);
                    ekycUserResponse.setPanDocType(panDocDTO);
                }
            }
            ekycUserResponse.setMessage(String.format("eKYC verification Status for %s ", ekycResponse.getIdType()));
            ekycUserResponse.setGender(ekycResponse.getGender());
            ekycUserResponse.setIdNo(ekycResponse.getIdNo());
            UserDocumentDTO aadhaarDocType = buildUserDocumentDTO(userDocument);
            aadhaarDocType.setDocumentData(null);
            ekycUserResponse.setAadhaarDocType(aadhaarDocType);
        }
        if (ekycResponse.getIdType().equalsIgnoreCase(CommonConstant.EKYC_PAN_DOC_TYPE)) {
            UserDocument aadhaarDoc = userDocumentRepository.findByUserAndDocType(user.getId(), CommonConstant.EKYC_AADHAR_DOC_TYPE).orElse(null);
            if (aadhaarDoc != null) {
                if (aadhaarDoc.getEkycStatus().equals(EkycStatus.COMPLETED) &&
                        userDocument.getEkycStatus().equals(EkycStatus.COMPLETED)) {
                    user.setEkycStatus(EkycStatus.COMPLETED);
                    userRepository.save(user);
                    ekycUserResponse.setMessage(String.format("eKYC Verification Status for AADHAAR"));
                    ekycUserResponse.setGender(ekycResponse.getGender());
                    ekycUserResponse.setIdNo(ekycResponse.getIdNo());
                    UserDocumentDTO aadhaarDocDto = buildUserDocumentDTO(aadhaarDoc);
                    ekycUserResponse.setAadhaarDocType(aadhaarDocDto);
                }
            }
            ekycUserResponse.setMessage(String.format("eKYC verification Status for %s ", ekycResponse.getIdType()));
            ekycUserResponse.setGender(ekycResponse.getGender());
            ekycUserResponse.setIdNo(ekycResponse.getIdNo());
            UserDocumentDTO panDocType = buildUserDocumentDTO(userDocument);
            panDocType.setDocumentData(null);
            ekycUserResponse.setPanDocType(panDocType);
        }
        return ekycUserResponse;
    }

    private List<UserDocError> buildUserDocError(UserDocument userDocument, List<UserDocError> userDocErrors) {
        return userDocErrors.stream().map(userDocError -> {
                    userDocError.setUserDocument(userDocument);
                    userDocError.setActive(Boolean.TRUE);
                    userDocError.setDeleted(Boolean.FALSE);
                    userDocError.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
                    return userDocError;
                }
        ).collect(Collectors.toList());
    }

    private void saveUserDocument(EkycResponse ekycResponse, User user) throws Exception {
        UserDocument userDocument = new UserDocument();
        userDocument.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        userDocument.setActive(Boolean.TRUE);
        userDocument.setDocumentData(ekycResponse.getEncodedImage());
        userDocument.setUser(user);
        userDocument.setIdNo(ekycResponse.getIdNo());
        userDocument.setDocType(ekycResponse.getIdType());
        ObjectMapper objectMapper = new ObjectMapper();
        String responseJson = objectMapper.writeValueAsString(ekycResponse);
        userDocument.setDocumentData(responseJson);
        userDocument.setEkycStatus(EkycStatus.IN_PROGRESS);
        userDocumentRepository.save(userDocument);
    }

    @Override
    public boolean isKycVerified(String userId) {
        Long count = userDocumentRepository.findIsVerified(userId);
        if (count == 2) {
            return Boolean.TRUE.booleanValue();
        }
        return Boolean.FALSE.booleanValue();
    }

    private void docValidation(MultipartFile frontPart, MultipartFile backPart) {
        if ((frontPart == null || frontPart.isEmpty()) && (backPart == null || backPart.isEmpty())) {
            throw new ActorPayException("Upload valid Front Part and Back part of Aadhaar");
        }
        if (frontPart == null || frontPart.isEmpty()) {
            throw new ActorPayException("Upload valid Front Part of Aadhaar");
        }
        if (backPart == null || backPart.isEmpty()) {
            throw new ActorPayException("Upload valid Back part of Aadhaar");
        }
    }

    private EkycUserResponse verifyEkyc(EkycResponse ekycResponse, User user, EkycRequest request) throws Exception {
        EkycUserResponse ekycUserResponse = new EkycUserResponse();
        saveUserDocument(ekycResponse, user);
        ekycUserResponse.setMessage(String.format("KYC verification successfully applied for the Document %s", ekycResponse.getIdType()));
        return ekycUserResponse;
    }

    @Override
    public UserDocument getKYCByDocType(String userId, String docType) {
        UserDocument userDocument = userDocumentRepository.findByUserAndDocType(userId, docType)
                .orElseThrow(() -> new ActorPayException(String.format("eKYC record not found for Doc Type %s ", docType)));
        return userDocument;
    }

    @Override
    public List<UserDocument> findByUser(String userId) {
        List<UserDocument> userDocuments = userDocumentRepository.findByUser(userId);
        return userDocuments;
    }

    @Override
    public UserDocumentDTO updateEkycStatus(EkycUpdateRequest ekycUpdateRequest) {
        String userId = ekycUpdateRequest.getUserId();
        UserDocument userDocument = userDocumentRepository
                .findByUserAndDocType(ekycUpdateRequest.getUserId(), ekycUpdateRequest.getDocType())
                .orElseThrow(() -> new ActorPayException(String.format("The Doc Type %s is not found", ekycUpdateRequest.getDocType())));
        User user = userRepository.findById(ekycUpdateRequest.getUserId()).orElseThrow(() -> new ActorPayException("User not found"));
        userDocument.setEkycStatus(ekycUpdateRequest.getStatus());
        userDocument.setReasonDescription(ekycUpdateRequest.getReason());
        userDocument = userDocumentRepository.saveAndFlush(userDocument);
        List<UserDocument> userDocuments = userDocumentRepository.findByUserAndDocTypeIn(user, CommonConstant.docTypes);
        UserDTO userDTO = UserTransformer.USER_TO_DTO.apply(user);
        Long count = userDocumentRepository.findIsVerified(ekycUpdateRequest.getUserId());
        if (count == 2) {
            userDTO.setAadhaarVerifyStatus(EkycStatus.COMPLETED);
            userDTO.setPanVerifyStatus(EkycStatus.COMPLETED);
            userDTO.setEkycStatus(EkycStatus.COMPLETED);
            user.setEkycStatus(EkycStatus.COMPLETED);
        } else {
            UserDocument aadhaar = userDocumentRepository.findByUserAndDocType(userId, CommonConstant.EKYC_AADHAR_DOC_TYPE).orElse(null);
            UserDocument pan = userDocumentRepository.findByUserAndDocType(userId, CommonConstant.EKYC_PAN_DOC_TYPE).orElse(null);
            if (aadhaar == null) {
                userDTO.setAadhaarVerifyStatus(EkycStatus.PENDING);
            } else {
                userDTO.setAadhaarVerifyStatus(aadhaar.getEkycStatus());
            }
            if (pan == null) {
                userDTO.setPanVerifyStatus(EkycStatus.PENDING);
            } else {
                userDTO.setPanVerifyStatus(pan.getEkycStatus());
            }
        }
        userRepository.save(user);
        UserDocumentDTO userDocumentDTO = buildUserDocumentDTO(userDocument);
        return userDocumentDTO;
    }

    @Override
    public PageItem<EkycFilterRequest> getAllEkyc(PagedItemInfo pagedInfo, EkycFilterRequest filterRequest) {

        GenericSpecificationsBuilder<UserDocument> builder = new GenericSpecificationsBuilder<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(User.class, pagedInfo);
        prepareMerchantSearchFilter(filterRequest, builder);
        Page<UserDocument> pagedResult = userDocumentRepository.findAll(builder.build(), pageRequest);
        List<EkycFilterRequest> content = pagedResult.getContent().stream().map(UserTransformer.USER_EKYC_TO_DTO)
                .collect(Collectors.toList());

        return new PageItem(pagedResult.getTotalPages(), pagedResult.getTotalElements(), content, pagedInfo.page,
                pagedInfo.items);
    }


    private void prepareMerchantSearchFilter(EkycFilterRequest filterRequest, GenericSpecificationsBuilder<UserDocument> builder) {

        builder.with(userSpecificationFactory.isEqual("deleted", false));

        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getDocType())) {
            builder.with(userSpecificationFactory.isEqual("docType", filterRequest.getDocType()));
        }
        /*if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getUser().getId())) {
            builder.with(userSpecificationFactory.isEqual("id", filterRequest.getUser().getId()));
        }*/
        if (filterRequest.getEkycStatus() != null) {
            builder.with(userSpecificationFactory.isEqual("ekycStatus", filterRequest.getEkycStatus()));
        }
        if (filterRequest.getStartDate() != null) {
            builder.with(userSpecificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().toLocalDate().atStartOfDay()));
        }
        if (filterRequest.getEndDate() != null) {
            builder.with(userSpecificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).toLocalDate().atStartOfDay()));
        }
    }

    private UserDocumentDTO buildUserDocumentDTO(UserDocument userDocument) {
        UserDocumentDTO userDocumentDTO = new UserDocumentDTO();
        userDocumentDTO.setId(userDocument.getId());
        userDocumentDTO.setIdNo(userDocument.getIdNo());
        userDocumentDTO.setDocumentData(userDocument.getDocumentData());
        userDocumentDTO.setUserId(userDocument.getUser().getId());
        userDocumentDTO.setDocType(userDocument.getDocType());
        userDocumentDTO.setEkycStatus(userDocument.getEkycStatus());
        userDocumentDTO.setCreatedAt(userDocument.getCreatedAt());
        userDocumentDTO.setUpdatedAt(userDocument.getUpdatedAt());
        List<UserDocError> userDocErrorList = userDocument.getDocErrorList();
        if (userDocErrorList != null) {
            List<UserDocErrorDTO> errorDTOList = userDocErrorList.stream().map(userDocError -> {
                        UserDocErrorDTO userDocErrorDTO = new UserDocErrorDTO();
                        userDocErrorDTO.setDocumentId(userDocument.getId());
                        userDocErrorDTO.setErrorMessage(userDocError.getErrorMessage());
                        return userDocErrorDTO;
                    }
            ).collect(Collectors.toList());
            userDocumentDTO.setUserDocErrors(errorDTOList);
        }
        return userDocumentDTO;
    }
}
