package com.octal.actorPay.service.impl;

import com.google.gson.Gson;
import com.octal.actorPay.client.AdminFeignClient;
import com.octal.actorPay.client.NotificationClient;
import com.octal.actorPay.constants.*;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.payments.OrderRequest;
import com.octal.actorPay.dto.request.MerchantFilterRequest;
import com.octal.actorPay.entities.*;
import com.octal.actorPay.exceptions.AccessDeniedException;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.external.SmsService;
import com.octal.actorPay.helper.MessageHelper;
import com.octal.actorPay.repositories.*;
import com.octal.actorPay.service.*;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.MerchantTransformer;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.utils.CommonUtils;
import com.octal.actorPay.utils.MerchantFeignHelper;
import com.octal.actorPay.utils.MerchantServiceCodeGenerator;
import com.octal.actorPay.utils.QRCodeGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class MerchantServiceImpl implements MerchantService {

    public static String tempOtp;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MerchantSubMerchantAssocRepository merchantSubMerchantAssocRepository;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MessageHelper messageHelper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserVerificationService userVerificationService;
    @Autowired
    private DefaultEmailService<User> emailService;
    @Autowired
    private UserVerificationRepository userVerificationRepository;
    @Autowired
    private MerchantDetailsRepository merchantDetailsRepository;
    @Autowired
    private AdminFeignClient adminFeignClient;
    @Autowired
    private MerchantFeignHelper merchantFeignHelper;
    @Autowired
    private SmsService smsService;
    @Autowired
    private MerchantSettingsRepository merchantSettingsRepository;
    @Autowired
    private QRCodeGenerator qrCodeGenerator;
    @Autowired
    private MerchantServiceCodeGenerator merchantServiceCodeGenerator;
    @Autowired
    private MerchantQrRepository merchantQrRepository;
    @Autowired
    private SpecificationFactory<User> userSpecificationFactory;
    @Autowired
    private MerchantEmailService merchantEmailService;
    @Autowired
    private OutletRepository outletRepository;
    @Autowired
    private RoleApiMappingRepository roleApiMappingRepository;

    @Autowired
    private UserDocumentRepository userDocumentRepository;

    @Autowired
    private MerchantWalletService merchantWalletService;

    @Autowired
    private NotificationClient notificationClient;

    @Override
    public User getUserByEmailId(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        // TODO, Here we will create a transformer to convert user object into dto
        return user.orElse(null);
    }

    @Override
    public void saveMerchantDeviceDetails(String userId, DeviceDetailsDTO deviceDetailsDTO) {
        if (Objects.nonNull(deviceDetailsDTO)) {
            User user = new User();
            user.setId(userId);
            MerchantDetails userDeviceDetails = merchantDetailsRepository.findByUser(user);
            if (Objects.isNull(userDeviceDetails)) {
                userDeviceDetails = new MerchantDetails();
                userDeviceDetails.setUser(userRepository.findById(userId).get());
            }
            userDeviceDetails.setDeviceType(deviceDetailsDTO.getDeviceType());
            userDeviceDetails.setDeviceToken(deviceDetailsDTO.getDeviceToken());
            userDeviceDetails.setDeviceData(deviceDetailsDTO.getDeviceData());
            userDeviceDetails.setAppVersion(deviceDetailsDTO.getAppVersion());
            merchantDetailsRepository.save(userDeviceDetails);
        }
    }

    @Override
    public AuthUserDTO fetchAuthenticatedUserDetailsByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(user1 -> MerchantTransformer.USER_TO_AUTH_DTO.apply(user1)).orElse(null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public User create(MerchantDTO merchantDTO, String currentUser) throws Exception {
        beforeCreateNewUser(merchantDTO);
        User user = new User();
        // find role
        if (merchantDTO.getResourceType().equals(ResourceType.SUB_MERCHANT)) {
            String randomPassword = String.valueOf(CommonUtils.getRandomNumber(100000));
            user.setPassword(randomPassword);
            merchantDTO.setRandomPassword(randomPassword);
            merchantDTO.setPassword(randomPassword);
            user.setTemp(randomPassword);
        }
        if (merchantDTO.getResourceType().equals(ResourceType.PRIMARY_MERCHANT)) {
            Role role = roleRepository.findByNameAndIsActiveTrue(Role.RoleName.PRIMARY_MERCHANT.name())
                    .orElseThrow(() -> new ActorPayException("Role not found"));
            user.setRole(role);
        } else {
            if (merchantDTO.getRoleId() == null || merchantDTO.getRoleId().isBlank())
                throw new ActorPayException("Role Id can't be Empty");
            Role role = roleRepository.findResourceType(merchantDTO.getRoleId()).orElseThrow(() -> new ActorPayException(String.format("Invalid Role Id %s ", merchantDTO.getRoleId())));
            user.setRole(role);
        }
        user.setGender(merchantDTO.getGender());
        user.setPassword(passwordEncoder.encode(merchantDTO.getPassword()));
        user.setIsMerchant(true);
        user.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setFirstName(merchantDTO.getFirstName());
        user.setLastName(merchantDTO.getLastName());
        if (merchantDTO.getDateOfBirth() != null) {
          /*  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(merchantDTO.getDateOfBirth(), formatter);*/
            user.setDateOfBirth(merchantDTO.getDateOfBirth());
        }
        MerchantDetails merchant = new MerchantDetails();
        merchant.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        merchant.setActive(true);
        merchant.setUser(user);
        user.setContactNumber(merchantDTO.getContactNumber());
        user.setUserType(CommonConstant.USER_TYPE_MERCHANT);
        user.setExtensionNumber(merchantDTO.getExtensionNumber());
        user.setPanNumber(merchantDTO.getPanNumber());
        user.setAadharNumber(merchantDTO.getAadhar());
        user.setEkycStatus(EkycStatus.PENDING);
        // user.setMerchantDetails(merchantDTO.getMerchantSettingsDTOS());
        merchant.setResourceType(merchantDTO.getResourceType());
        mapUserData(user, merchantDTO, merchant);
        user.setActive(merchantDTO.getActive());
        if (merchantDTO.getResourceType().equals(ResourceType.PRIMARY_MERCHANT)) {
            List<String> keys = Arrays.asList(CommonConstant.CANCELLATION_FEE, CommonConstant.RETURN_FEE,
                    CommonConstant.ADMIN_COMMISSION, CommonConstant.RETURN_DAYS);
            List<SystemConfigurationDTO> sysConfigs = merchantFeignHelper.getMerchantSettings(keys);
            if (sysConfigs != null && sysConfigs.size() > 0) {
                List<MerchantSettings> merchantSettingsList = new ArrayList<>();
                for (SystemConfigurationDTO sysconfg : sysConfigs) {
                    MerchantSettings merchantSettings = new MerchantSettings();
                    merchantSettings.setMerchantDetails(merchant);
                    merchantSettings.setActive(Boolean.TRUE);
                    merchantSettings.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
                    merchantSettings.setParamDescription(sysconfg.getParamDescription());
                    merchantSettings.setParamName(sysconfg.getParamName());
                    merchantSettings.setParamValue(sysconfg.getParamValue());
                    merchantSettingsList.add(merchantSettings);
                }
                System.out.println("Sys Config " + sysConfigs);
                user.getMerchantDetails().setMerchantSettings(merchantSettingsList);
            }
        }
        User newUser = userRepository.save(user);
        Outlet outlet = new Outlet();
        outlet.setContactNumber(merchantDTO.getContactNumber());
        outlet.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        outlet.setActive(Boolean.TRUE);
//        outlet.setAddress(merchantDTO.getShopAddress());
        outlet.setTitle(merchantDTO.getBusinessName());
        outlet.setExtensionNumber(merchantDTO.getExtensionNumber());
        outlet.setLicenceNumber(merchantDTO.getLicenceNumber());
        outlet.setMerchantDetails(newUser.getMerchantDetails());
        outletRepository.save(outlet);
        // generated otp token
        System.out.println("##### Random Password ###### " + merchantDTO.getRandomPassword());
        UserOtpVerification userVerificationToken = userVerificationService.createUserVerificationToken(user);
        // TODO: need to uncomment below lines of code
        try {
            merchantEmailService.sendEmailOnNewUserRegistrationByMerchant(user, userVerificationToken.getToken(), merchantDTO.getRandomPassword());
        } catch (Exception e) {
            System.out.println("Error While Sending Mail to User!! " + e.getMessage());
        }
        return newUser;
    }

    //    private void addPermissionToMerchant(ResourceType resourceType,User user) {
//        MerchantDetails merchantDetails = user.getMerchantDetails();
//
//        if(merchantDetails.getResourceType().equals(ResourceType.PRIMARY_MERCHANT)) {
//            Role role = roleRepository.findByName(ResourceType.PRIMARY_MERCHANT.name());
//            List<RoleApiMapping> roleApiMappings = roleApiMappingRepository.findByRoleId(role.getId());
//        }
//    }
    private void beforeCreateNewUser(MerchantDTO request) {
        Optional<User> userExist = userRepository.findByEmail(request.getEmail());

        if (userExist.isPresent()) {
            throw new ActorPayException(messageHelper.getMessage("email.already.exist", new Object[]{request.getEmail()}));
        }
        Optional<User> user = userRepository.findUserByContactNumber(request.getContactNumber());
        if (user.isPresent()) {
            throw new ActorPayException(messageHelper.getMessage("contact.number.already.exist", new Object[]{request.getContactNumber()}));
        }
        if (!request.getResourceType().equals(ResourceType.SUB_MERCHANT)) {
            Long bizExist = merchantDetailsRepository.countByBusinessNameAndDeletedFalse(request.getBusinessName());
            if (bizExist > 0) {
                throw new ActorPayException(String.format("Given Business Name is already exist %s ", request.getBusinessName()));
            }
        }
//        Long aadhaarCount = userRepository.findByAadhaarNumberCount(request.getAadhar());
//        if(aadhaarCount > 0) {
//            throw  new ActorPayException("You Aadhaar is already available in the System");
//        }
//        Long panCount = userRepository.findByPanNumberCount(request.getPanNumber());
//        if(panCount > 0) {
//            throw new ActorPayException("You Pan is already available in the System");
//        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void update(MerchantDTO userDTO, String currentUserEmailId, String userType, MultipartFile file) throws IOException {
        User user = null;
        User currentUser = userRepository.findByEmail(currentUserEmailId).orElseThrow(() -> new RuntimeException("Invalid user "));
        user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new RuntimeException("User is not found"));
        if (!currentUser.getEmail().equalsIgnoreCase(user.getEmail())) {
            throw new RuntimeException("Invalid user login to update");
        }
        if (userDTO.getResourceType().equals(ResourceType.PRIMARY_MERCHANT)) {
            List<MerchantSettingsDTO> merchantSettingsDTOList = userDTO.getMerchantSettingsDTOS();
            List<String> keys = merchantSettingsDTOList.stream().map(key -> key.getParamName()).collect(Collectors.toList());
            for (String key : keys) {
                if (!CommonConstant.merchantSettings.contains(key)) {
                    throw new RuntimeException(String.format("Invalid merchant setting value %s ", key));
                }
            }
            for (MerchantSettingsDTO dto : merchantSettingsDTOList) {
                if (StringUtils.isBlank(dto.getParamValue())) {
                    throw new RuntimeException(String.format("%s  key value is can't be empty ", dto.getParamName()));
                }
            }
            if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_MERCHANT)) {
                SystemConfigurationDTO systemConfigurationDTO =
                        merchantFeignHelper.getConfigByKey(CommonConstant.ADMIN_COMMISSION);
                double adminCommission = Double.parseDouble(systemConfigurationDTO.getParamValue());
                for (MerchantSettingsDTO dto : merchantSettingsDTOList) {
                    if (CommonConstant.ADMIN_COMMISSION.equalsIgnoreCase(dto.getParamName())) {
                        double commission = Double.parseDouble(dto.getParamValue());
                        if (commission != adminCommission) {
                            throw new RuntimeException(String.format("Commission value cannot be changed"));
                        }
                    }
                }
            } else if (userType.equalsIgnoreCase(CommonConstant.USER_TYPE_ADMIN)) {
                user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new RuntimeException("User id not found"));
            }
        }
        if (file != null) {
        String s3Path = uploadService.uploadFileToS3(file, "merchant");
        user.setProfilePicture(s3Path);
        }
        mapUserData(user, userDTO, user.getMerchantDetails());
        if (userDTO.getMerchantType() != null) {
            user.getMerchantDetails().setMerchantType(userDTO.getMerchantType());
        }
        userRepository.save(user);
    }


    /**
     * this method is used to mapUserData user object
     *
     * @param user        - user entity
     * @param merchantDTO - userDto object contains user related property
     */
    private void mapUserData(User user, MerchantDTO merchantDTO, MerchantDetails merchant) {
        user.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        merchant.setBusinessName(merchantDTO.getBusinessName());
        merchant.setLicenceNumber(merchantDTO.getLicenceNumber());
        merchant.setFullAddress(merchantDTO.getFullAddress());
        merchant.setShopAddress(merchantDTO.getShopAddress());
        merchant.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        merchant.setMerchantType(merchantDTO.getMerchantType());
        merchant.setResourceType(merchantDTO.getResourceType());
        user.setEmail(merchantDTO.getEmail());
        user.setFirstName(merchantDTO.getFirstName());
        user.setLastName(merchantDTO.getLastName());
        user.setContactNumber(merchantDTO.getContactNumber());
        if (merchant.getResourceType().equals(ResourceType.PRIMARY_MERCHANT)) {
            List<MerchantSettingsDTO> merchantSettingsDTOS = merchantDTO.getMerchantSettingsDTOS();
            List<MerchantSettings> merchantSettingsList = new ArrayList<>();
            if (merchantSettingsDTOS != null && merchantSettingsDTOS.size() > 0) {
                for (MerchantSettingsDTO merchantSettingsDTO : merchantSettingsDTOS) {
                    MerchantSettings merchantSettings = new MerchantSettings();
                    merchantSettings.setId(merchantSettingsDTO.getId());
                    merchantSettings.setParamName(merchantSettingsDTO.getParamName());
                    merchantSettings.setParamValue(merchantSettingsDTO.getParamValue());
                    merchantSettings.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                    merchantSettings.setActive(Boolean.TRUE);
                    merchantSettings.setParamDescription(merchantSettingsDTO.getParamDescription());
                    merchantSettings.setMerchantDetails(merchant);
                    merchantSettingsList.add(merchantSettings);
                }
            }
            merchant.setMerchantSettings(merchantSettingsList);
        }
        user.setMerchantDetails(merchant);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(List<String> ids, String currentUser) throws InterruptedException {
        User loggedInUser = userRepository.findByEmail(currentUser).orElseThrow(() -> new ActorPayException("User not found"));
        if (loggedInUser != null) {
            List<User> users = userRepository.findAllById(ids);
            Thread.sleep(500);
            userRepository.deleteInBatch(users);
        } else {
            throw new AccessDeniedException(messageHelper.getMessage(messageHelper.getMessage("admin.can.access.this.resource")));
        }
    }

    @Override
    public MerchantDTO getMerchantDetails(String userId, String currentUser) {
        MerchantDTO merchantDTO = null;
        List<String> docTypes = Arrays.asList(CommonConstant.EKYC_AADHAR_DOC_TYPE, CommonConstant.EKYC_PAN_DOC_TYPE);
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            merchantDTO = user.map(user1 -> MerchantTransformer.MERCHANT_TO_DTO.apply(user1)).orElse(null);
            merchantDTO.setParentId(currentUser);

            merchantDTO.setAadhaarVerifyStatus(EkycStatus.PENDING);
            merchantDTO.setPanVerifyStatus(EkycStatus.PENDING);
            List<UserDocument> userDocuments = userDocumentRepository.findByUserAndDocTypeIn(user.get(), CommonConstant.docTypes);
            if (userDocuments != null) {
                UserDocument aadhaarDoc = userDocuments.stream().
                        filter(u -> u.getDocType().equalsIgnoreCase(CommonConstant.EKYC_AADHAR_DOC_TYPE)).findAny().orElse(null);
                UserDocument panDoc = userDocuments.stream().
                        filter(u -> u.getDocType().equalsIgnoreCase(CommonConstant.EKYC_PAN_DOC_TYPE)).findAny().orElse(null);
                if (aadhaarDoc != null)
                    merchantDTO.setAadhaarVerifyStatus(aadhaarDoc.getEkycStatus());
                if (panDoc != null)
                    merchantDTO.setPanVerifyStatus(panDoc.getEkycStatus());
            }
        } else {
            throw new ObjectNotFoundException("User not found for the given id: " + userId);
        }

        merchantDTO.setMerchantName(user.get().getFirstName() + "  " + user.get().getLastName());
        merchantDTO.setDateOfBirth(user.get().getDateOfBirth());
        return merchantDTO;
    }

    @Override
    public MerchantDTO getMerchantDetailsByMerchantId(String merchantId) {
        MerchantDetails merchantDetails = merchantDetailsRepository.findByIdAndDeletedFalse(merchantId);
        MerchantDTO merchantDTO = MerchantTransformer.MERCHANT_ENTITY_TO_DTO.apply(merchantDetails);
        return merchantDTO;
    }

    @Override
    public List<MerchantDTO> getAllMerchant(Boolean filterByIsActive, String sortBy, boolean asc) {
        List<MerchantDetails> merchantDetails = null;
        if (asc) {
            merchantDetails = merchantDetailsRepository
                    .findByDeletedFalseAndIsActive(filterByIsActive, Sort.by(sortBy).ascending());
        } else {
            merchantDetails = merchantDetailsRepository
                    .findByDeletedFalseAndIsActive(filterByIsActive, Sort.by(sortBy).descending());
        }
        List<MerchantDTO> merchantDTOS = new ArrayList<>();
        for (MerchantDetails merchantDetail : merchantDetails) {
            MerchantDTO merchantDTO = MerchantTransformer.MERCHANT_ENTITY_TO_DTO.apply(merchantDetail);
            merchantDTOS.add(merchantDTO);
        }
        return merchantDTOS;
    }

    @Transactional
    @Override
    public void updateMerchantSettingByMerchantType(String paramName, String paramValue, MerchantType merchantType) {
        merchantSettingsRepository.updateMerchantSettingByMerchantType(paramName, paramValue, merchantType);
    }

    @Override
    @Transactional
    public PageItem<MerchantDTO> getAllSubMerchantsByMerchant(PagedItemInfo pagedInfo, MerchantFilterRequest filterRequest, String loggedInMerchant) throws Exception {
        //        Optional<User> actor = userRepository.findByEmail(currentUser);
        List<User> subMerchantResponseList = new ArrayList<>();
        Optional<User> merchant = userRepository.findByEmail(loggedInMerchant);
        if (!merchant.isPresent())
            throw new Exception("Invalid merchant!!");
        if (merchant.get().getMerchantDetails() != null) {
            List<MerchantSubMerchantAssoc> list = merchantSubMerchantAssocRepository.findByMerchantId(merchant.get().getMerchantDetails().getId());
            for (MerchantSubMerchantAssoc merchantSubMerchantAssoc : list) {
                Optional<MerchantDetails> subMerchantDetails = merchantDetailsRepository.findById(merchantSubMerchantAssoc.getSubmerchantId());
                if (subMerchantDetails.isPresent()) {
                    subMerchantResponseList.add(subMerchantDetails.get().getUser());
                }
            }
        }
//        GenericSpecificationsBuilder<User> builder = new GenericSpecificationsBuilder<>();
//        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(User.class, pagedInfo);
////        Page<User> pagedResult = userRepository.findAllByIsMerchantIsTrue(pageRequest);
//        prepareMerchantSearchFilter(filterRequest, builder);
////        Page<User> pagedResult = orderDetailsRepository.findAll(builder.build(), pageRequest);
//        user.getMerchantDetails()

//        Page<User> pagedResult = userRepository.findAll(builder.build(), pageRequest);
        List<MerchantDTO> content = subMerchantResponseList.stream().map(MerchantTransformer.MERCHANT_TO_DTO)
                .collect(Collectors.toList());
        if(subMerchantResponseList.size() > 0){
            return new PageItem<>(subMerchantResponseList.size() / (pagedInfo.page + 1), subMerchantResponseList.size(), content, pagedInfo.page,
                    pagedInfo.items);
        }else{
            return new PageItem<>(0, 0, content, pagedInfo.page,
                    pagedInfo.items);

        }
    }

    @Override
    public PageItem<MerchantDTO> getAllMerchantsPaged(PagedItemInfo pagedInfo, MerchantFilterRequest filterRequest) {
//        Optional<User> actor = userRepository.findByEmail(currentUser);

        GenericSpecificationsBuilder<User> builder = new GenericSpecificationsBuilder<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(User.class, pagedInfo);
//        Page<User> pagedResult = userRepository.findAllByIsMerchantIsTrue(pageRequest);
        prepareMerchantSearchFilter(filterRequest, builder);
//        Page<User> pagedResult = orderDetailsRepository.findAll(builder.build(), pageRequest);
        Page<User> pagedResult = userRepository.findAll(builder.build(), pageRequest);
        List<MerchantDTO> content = pagedResult.getContent().stream().map(MerchantTransformer.MERCHANT_TO_DTO)
                .collect(Collectors.toList());
        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), content, pagedInfo.page,
                pagedInfo.items);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeMerchantPassword(ChangePasswordDTO changePasswordDTO, String userName) {
        // new password and confirm password should be same
        if (changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            // get user from user id
            Optional<User> user = userRepository.findByEmail(userName);
            if (user.isPresent()) {
                // Validate user with current password, if user has entered wrong current password then throw error
                if (passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.get().getPassword())) {
                    // encode user password
                    user.get().setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
                    user.get().setUpdatedAt(LocalDateTime.now());
                    userRepository.save(user.get());
                } else {
                    throw new ActorPayException(messageHelper.getMessage("invalid.current.password"));
                }
            }
        } else {
            throw new ActorPayException(messageHelper.getMessage("new.confirm.password.mismatch"));
        }
    }

    @Override
    public UserOtpVerification.UserVerificationStatus getUserVerificationStatus(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new ActorPayException(String.format("Invalid User Credential: %s", email)));
        List<UserOtpVerification> userVerificationObjs = userVerificationRepository.findByTypeAndUserIdForVerification(UserOtpVerification.Types.NEW_USER, user.getId());
        boolean isPresent = userVerificationObjs
                .stream()
                .filter(c -> c.getUserVerificationStatus() == UserOtpVerification.UserVerificationStatus.STATUS_PENDING)
                .findAny().isPresent();

        if (isPresent) {
            return UserOtpVerification.UserVerificationStatus.STATUS_PENDING;
        }
        UserOtpVerification userOtpVerification = userVerificationObjs
                .stream()
                .filter(c -> c.getUserVerificationStatus() == UserOtpVerification.UserVerificationStatus.STATUS_VERIFIED)
                .findAny().get();
        return userOtpVerification.getUserVerificationStatus();
    }

    @Override
    public boolean isUserAccountActive(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ActorPayException("Invalid login details: " + email));
        return user.isActive() == null ? false : user.isActive();
    }

    @Override
    public boolean activeNewUserByToken(String token) {
        Optional<UserOtpVerification> userVerificationObj = userVerificationRepository.findByToken(token);

        if (userVerificationObj.isPresent()) {
//            User user = userVerificationObj.get().getUser();
            User user = userRepository.findById(userVerificationObj.get().getUserId()).orElseThrow(() ->
                    new ActorPayException("User not found for given id: " + userVerificationObj.get().getUserId()));
            if (user != null) {
                // mark active to user account
                user.setActive(true);
                user.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                user.setEmailVerified(true);
                // update user object
                userRepository.save(user);
                // update token status
                updateUserVerificationStatus(userVerificationObj, user);
                return true;
            } else {
                return false;
            }
        } else {
            throw new ActorPayException("verification.token.is.not.valid");
        }
    }

    private void updateUserVerificationStatus(Optional<UserOtpVerification> userVerificationToken, User user) {

        if (userVerificationToken.isPresent()) {
            // check verification token is expired or not, if token is expired then throw
            // exception
            if (LocalDateTime.now().isBefore(userVerificationToken.get().getExpiredDateTime())) {

                // update user verification token status
                userVerificationToken.get().setUserVerificationStatus(UserOtpVerification.UserVerificationStatus.STATUS_VERIFIED);
                userVerificationToken.get().setConfirmedDateTime(LocalDateTime.now(ZoneOffset.UTC));
                userVerificationToken.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

                // make confirmation token expired
                userVerificationToken.get()
                        .setExpiredDateTime(userVerificationToken.get().getExpiredDateTime().minusDays(2));
                // TODO, check with status only and remove active flag, need to optimize this method
                userVerificationToken.get().setActive(false);

//                user.setUserOtpVerification(userVerificationToken.get());
                userVerificationRepository.save(userVerificationToken.get());

            } else {
                throw new ActorPayException("Security key is expired");
            }
        }

    }

    @Override
    public void resetUserPassword(String email) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new ActorPayException(String.format("User not found for given email id: %s", email)));
        // if user is present then send email with generated OTP
        if (user != null) {
            userVerificationService.generateUserOtp(user, UserOtpVerification.Types.FORGOT_PASSWORD);
        }
    }

    @Override
    public void resetUserPassword(String token, String newPassword, String confirmPassword) {
        Optional<UserOtpVerification> userVerificationToken = userVerificationRepository.findByToken(token);

        if (userVerificationToken.isPresent()) {

//            User user = userVerificationToken.get().getUser();
            User user = userRepository.findById(userVerificationToken.get().getUserId()).orElseThrow(() -> new
                    ActorPayException(String.format("User not found for given id: %s " + userVerificationToken.get().getUserId())));

            if (user != null) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                // update user object
                userRepository.save(user);
                updateUserVerificationStatus(userVerificationToken, user);

            } else {
                throw new ActorPayException("Invalid Request, User object not not found");
            }
        }
    }

    @Override
    public MerchantDetails getMerchantIdByEmail(String email) {

        MerchantDetails merchantDetails = merchantDetailsRepository.findMerchantIdByEmail(email);
        return merchantDetails;
    }

//    @Override
//    public Optional<Collaborators> getSubMerchantById(String userId) {
//
////        Collaborators subMerchantDetails = collaboratorsRepository.findSubMerchantIdByUserId(userId);
////        return Optional.ofNullable(subMerchantDetails);
//        return null;
//    }

    @Override
    public Boolean verifyUserContactNumberByOTP(String otp) {
        Boolean isVerified = false;
        Optional<UserOtpVerification> userVerificationObj = userVerificationRepository.findByOtp(otp);
        if (userVerificationObj.isPresent()) {
            UserOtpVerification userOtpVerification = userVerificationObj.get();
            if (userOtpVerification.getUserVerificationStatus().equals(UserOtpVerification.UserVerificationStatus.STATUS_VERIFIED)) {
                throw new ActorPayException("Merchant Mobile is already verified");
            }
            User user = userRepository.findById(userVerificationObj.get().getUserId()).orElseThrow(() ->
                    new ActorPayException(String.format("No user is not associated with this otp: %s ", otp)));
            System.out.println("Current Time ### " + LocalDateTime.now());
            System.out.println("Expired Time ### " + userVerificationObj.get().getExpiredDateTime());
            if (LocalDateTime.now().isBefore(userVerificationObj.get().getExpiredDateTime())) {
                // expired
                throw new ActorPayException("Invalid request, OTP is expired ");
            } else {
                user.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                user.setPhoneVerified(Boolean.TRUE);
                // update user verification token status
                userVerificationObj.get().setUserVerificationStatus(UserOtpVerification.UserVerificationStatus.STATUS_VERIFIED);
                userVerificationObj.get().setConfirmedDateTime(LocalDateTime.now(ZoneOffset.UTC));
                userVerificationObj.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

                // make confirmation token expired
                userVerificationObj.get()
                        .setExpiredDateTime(userVerificationObj.get().getExpiredDateTime().minusDays(1));
                userVerificationObj.get().setActive(Boolean.FALSE);

                // update user object
                userRepository.save(user);
                userVerificationRepository.save(userVerificationObj.get());
                isVerified = true;

            }
        }
        return isVerified;
    }

    @Override
    public void sendOTPToVerifyPhoneNumber(String loggedInUserEmail) {
        Optional<User> loggedInUser = userRepository.findByEmail(loggedInUserEmail);
        if (loggedInUser.isPresent()) {
            UserOtpVerification userOtpVerification = userVerificationService.saveUserPhoneVerificationDetails(loggedInUser.get());
            List<String> sendMsg = Stream.of(loggedInUser.get().getExtensionNumber() + loggedInUser.get().getContactNumber())
                    .collect(Collectors.toList());
            tempOtp = userOtpVerification.getOtp();
            smsService.sendSMSNotification(userOtpVerification.getOtp(), sendMsg);

        }
    }

    @Override
    public String findMerchantName(String merchantId) {
        return merchantDetailsRepository.findMerchantName(merchantId);
    }

    @Override
    public MerchantDTO findByMerchantBusinessName(String merchantName) {
        MerchantDetails merchantDetails = merchantDetailsRepository.findByBusinessNameAndDeletedFalse(merchantName);
        return MerchantTransformer.MERCHANT_ENTITY_TO_DTO.apply(merchantDetails);
    }

    @Override
    public MerchantQRDTO processQRCode(String email) throws Exception {
        byte[] qrCode;
        MerchantQRDTO merchantQRDTO = null;
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (StringUtils.isBlank(user.getUpiQrCode())) {
                MerchantQR merchantQR = new MerchantQR();
                merchantQR.setMerchantId(user.getMerchantDetails().getId());
                merchantQR.setUpiQrCode(merchantServiceCodeGenerator.getUPICode());
                merchantQR.setActive(Boolean.TRUE);
                merchantQR.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
                merchantQR.setMerchantUserId(user.getId());
                qrCode = qrCodeGenerator.generateQRCodeImage(merchantQR.getUpiQrCode());
                merchantQR.setUpiQrImage(qrCode);
                MerchantQR newMerchantQR = merchantQrRepository.save(merchantQR);
                user.setUpiQrCode(merchantQR.getUpiQrCode());
                userRepository.save(user);
                merchantQRDTO = MerchantTransformer.MERCHANT_OR_ENTITY_TO_DTO.apply(merchantQR);
            } else {
                MerchantQR merchantQR = merchantQrRepository.findByUpiQrCode(user.getUpiQrCode());
                merchantQRDTO = MerchantTransformer.MERCHANT_OR_ENTITY_TO_DTO.apply(merchantQR);
            }
        } else {
            throw new RuntimeException("Invalid User");
        }
        return merchantQRDTO;
    }

    @Override
    public UserDTO findUserByEmailOrContactNumber(String userIdentity) {
        User user = userRepository.findUserByEmailOrContactNumber(userIdentity).orElse(null);
        if (user != null) {
            UserDTO userDTO = MerchantTransformer.USER_TO_USER_DTO.apply(user);
            return userDTO;
        }
        return null;
    }

    @Override
    public Optional<CommonUserDTO> findUserIdentity(String userIdentity) {
        User user = userRepository.findUserByEmailOrContactNumber(userIdentity).orElse(null);
        if (user == null) {
            throw new RuntimeException(String.format("Invalid user Identity %s ", userIdentity));
        }
        CommonUserDTO commonUserDTO = new CommonUserDTO();
        commonUserDTO.setUserId(user.getId());
        commonUserDTO.setFirstName(user.getFirstName());
        commonUserDTO.setLastName(user.getLastName());
        commonUserDTO.setEmail(user.getEmail());
        commonUserDTO.setExtension(user.getExtensionNumber());
        commonUserDTO.setContactNumber(user.getContactNumber());
        commonUserDTO.setLastName(user.getLastName());
        ;
        commonUserDTO.setBusinessName(user.getMerchantDetails().getBusinessName());
        commonUserDTO.setMerchantId(user.getMerchantDetails().getId());
        commonUserDTO.setUserType(user.getUserType());
        return Optional.of(commonUserDTO);
    }

    @Override
    public MerchantDTO getMerchantByUserId(String userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId).orElse(null);
        MerchantDTO merchantDTO = MerchantTransformer.MERCHANT_TO_DTO.apply(user);
        return merchantDTO;
    }

    @Override
    public MerchantResponse findMerchantBasicData(String email) {
        return merchantDetailsRepository.findMerchantBasicData(email);
    }

    @Override
    public User findByPrimaryMerchantBySubMerchantId(String subMerchantId) {
        return userRepository.findByPrimaryMerchantBySubMerchantId(subMerchantId)
                .orElseThrow(() -> new ActorPayException("User not found"));
    }

    @Override
    public ApiResponse checkout(OrderRequest orderRequest) {
        orderRequest.setAmount(orderRequest.getAmount());
        orderRequest.setCurrency(PGConstant.IND_CURRENCY);
        ApiResponse apiResponse = merchantWalletService.createOrder(orderRequest);
        return apiResponse;
    }

    @Override
    public long getAllMerchantsCount() {
        return merchantDetailsRepository.count();
    }

//    @Override
//    public Long findAadhaarCount(String email, String aadharNumber) {
//        return userRepository.findAadhaarCount(email,aadharNumber);
//    }

//    @Override
//    public Long findPanCount(String email, String panNumber) {
//        return userRepository.findPanCount(email,panNumber);
//    }

    private void prepareMerchantSearchFilter(MerchantFilterRequest
                                                     filterRequest, GenericSpecificationsBuilder<User> builder) {

        builder.with(userSpecificationFactory.isEqual("deleted", false));
        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getEmail())) {
            builder.with(userSpecificationFactory.isEqual("email", filterRequest.getEmail()));
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getResourceType())) {
            builder.with(userSpecificationFactory.isEqual("resourceType", filterRequest.getResourceType()));
        }
        if (filterRequest.getActive() != null) {
            builder.with(userSpecificationFactory.isEqual("active", filterRequest.getActive()));
        }
        if (StringUtils.isNotBlank(filterRequest.getContactNumber())) {
            builder.with(userSpecificationFactory.isEqual("contactNumber", filterRequest.getContactNumber()));
        }
        if (StringUtils.isNotBlank(filterRequest.getFirstName())) {
//            builder.with(userSpecificationFactory.isEqual("merchantDetails.businessName", filterRequest.getName()));
        //    builder.with(userSpecificationFactory.join("merchantDetails", "businessName", filterRequest.getName()));
            builder.with(userSpecificationFactory.isEqual("firstName",filterRequest.getFirstName()));
        }
        if (filterRequest.getStartDate() != null) {
            builder.with(userSpecificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
        }
        if (filterRequest.getEndDate() != null) {
            builder.with(userSpecificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).atStartOfDay()));
        }
        if (filterRequest.getEmailVerified() != null) {
            builder.with(userSpecificationFactory.isLessThanOrEquals("emailVerified", filterRequest.getEmailVerified()));
        }
        if (filterRequest.getPhoneVerified() != null) {
            builder.with(userSpecificationFactory.isLessThanOrEquals("phoneVerified", filterRequest.getPhoneVerified()));
        }

    }

    @Override
    public UserNotificationListDTO getUserNotificationList(User loggedInUser, UserNotificationListDTO.ListRequest request) throws Exception {
        UserNotificationListDTO responseData = new UserNotificationListDTO();
        ApiResponse result = notificationClient.getUserNotificationList(request);
        if (result.getStatus().equalsIgnoreCase("101"))
            throw new Exception("Notification not found");
        else if (result.getStatus().equalsIgnoreCase(HttpStatus.BAD_REQUEST.value() + "")) {
            throw new IllegalArgumentException(result.getMessage());
        }
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(result.getData());
        UserNotificationListDTO response = gson.fromJson(jsonResponse, UserNotificationListDTO.class);
        for (UserNotificationListDTO.Record record : response.getItems()) {
            Optional<User> notificationByUser = userRepository.findById(record.getUserId());
            if (notificationByUser.isPresent()) {
                record.setUserId(notificationByUser.get().getId());
                record.setUserImageUrl(notificationByUser.get().getProfilePicture());
                record.setUserName(notificationByUser.get().getFirstName());
                record.setTypeId(notificationByUser.get().getId());
            }
        }
        return response;
    }
}
