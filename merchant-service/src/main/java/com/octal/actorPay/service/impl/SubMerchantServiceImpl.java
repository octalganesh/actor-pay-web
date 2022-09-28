package com.octal.actorPay.service.impl;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.ResourceType;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.SubmerchantFilterRequest;
import com.octal.actorPay.entities.*;
import com.octal.actorPay.exceptions.AccessDeniedException;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.helper.MessageHelper;
import com.octal.actorPay.repositories.*;
import com.octal.actorPay.service.*;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import feign.FeignException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.octal.actorPay.transformer.SubMerchantTransformer.SUB_MERCHANT_TO_DTO;

@Service
public class SubMerchantServiceImpl implements SubMerchantService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MessageHelper messageHelper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserVerificationService userVerificationService;

    @Autowired
    private MerchantEmailService emailService;


    @Autowired
    private SpecificationFactory<User> userSpecificationFactory;

    @Autowired
    private MerchantDetailsRepository merchantDetailsRepository;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MerchantSubMerchantAssocRepository merchantSubMerchantAssocRepository;

    @Override
    public User getUserByEmailId(String email) {
        return null;
    }


    @Autowired
    private MerchantOutletRepository merchantOutletRepository;

    @Autowired
    private OutletRepository outletRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public User create(SubMerchantDTO subMerchantDTO, String currentUser) {
        try {
            System.out.println("Submerchant using merchant service");
            subMerchantDTO.setResourceType(ResourceType.SUB_MERCHANT);
            subMerchantDTO.setDefaultPassword(true);
            MerchantDetails merchantDetails = merchantDetailsRepository.findMerchantIdByEmail(currentUser);
            subMerchantDTO.setBusinessName(merchantDetails.getBusinessName());
            subMerchantDTO.setLicenceNumber(merchantDetails.getLicenceNumber());
            subMerchantDTO.setUserType(CommonConstant.USER_TYPE_MERCHANT);
            User registered = merchantService.create(subMerchantDTO, currentUser);
            MerchantSubMerchantAssoc merchantSubMerchantAssoc = new MerchantSubMerchantAssoc();
            merchantSubMerchantAssoc.setMerchantId(merchantDetails.getId());
            merchantSubMerchantAssoc.setSubmerchantId(registered.getMerchantDetails().getId());
            merchantSubMerchantAssoc.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            merchantSubMerchantAssoc.setActive(Boolean.TRUE);
            merchantSubMerchantAssocRepository.save(merchantSubMerchantAssoc);
            return registered;
        } catch (FeignException fe) {
            fe.printStackTrace();
            throw new RuntimeException(fe.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void update(SubMerchantDTO merchantDTO, String currentUser) throws Exception {
        Optional<User> subMerchantUser = userRepository.findById(merchantDTO.getMerchantId());
        if(!subMerchantUser.isPresent())
            throw new Exception("Invalid Id");
        MerchantDetails merchantDetails = merchantDetailsRepository
                .findByUser(subMerchantUser.get());
        merchantDetails.setFullAddress(merchantDTO.getFullAddress());
        merchantDetails.getUser().setFirstName(merchantDTO.getFirstName());
        merchantDetails.getUser().setLastName(merchantDTO.getLastName());
        merchantDetails.getUser().setContactNumber(merchantDTO.getContactNumber());
        merchantDetails.getUser().setEmail(merchantDTO.getEmail());
        merchantDetails.getUser().setGender(merchantDTO.getGender());
        merchantDetailsRepository.save(merchantDetails);
    }

    @Override
    public Map delete(List<String> ids, String currentUser) {
        Map<String, Object> data = new HashMap<>();
        for (String id : ids) {
            Optional<User> subMerchantUser = userRepository.findById(id);
            if(subMerchantUser.isPresent()) {
                subMerchantUser.get().setDeleted(true);
                userRepository.save(subMerchantUser.get());
                data.put(id, "Deleted Successfully");
            }
            else {
                data.put(id, "User Id not found");
            }
        }
        return data;
    }

    //
    @Override
    public SubMerchantDTO getSubMerchantDetails(String id, String currentUser) {
      User actor = userRepository.findByEmail(currentUser).orElseThrow(()-> new ActorPayException("User not found"));
        User subMerchant = userRepository.findByIdAndDeletedFalse(id).orElseThrow(()-> new ActorPayException("Sub merchant not found for the give id: " + id));
        List<RoleDTO> allRoles = roleService.getAllRoles(currentUser);

        if (subMerchant.getEmail().equals(subMerchant.getEmail()) || actor.getMerchant()) {
            SubMerchantDTO subMerchantDTO = SUB_MERCHANT_TO_DTO.apply(subMerchant);
//            subMerchantDTO.setRoleId(allRoles.stream().filter(roleDTO -> roleDTO.getId().equals(subMerchant.get().getRoles().stream().findFirst().get().getId()))
//                    .findFirst().get().getId());
//            Collaborators collaborators = collaboratorsRepository.findCollaboratorsByUserId(id);
//            subMerchantDTO.setMerchantId(collaborators.getMerchantId());
            return subMerchantDTO;
        } else {
            throw new ObjectNotFoundException("Access denied!! Only account owner and merchant can read sub merchant details");
        }
    }

    @Override
    public PageItem<SubMerchantDTO> getAllSubMerchantsPaged(PagedItemInfo pagedInfo, SubmerchantFilterRequest submerchantFilterRequest, String currentUser) {

        User user = userRepository.findByEmail(currentUser).orElseThrow(() -> new ActorPayException("Access denied!! Invalid User found"));  //, "Access denied!! Invalid User found");
        if (user != null) {
            if (!user.getRole().getName().equals(Role.RoleName.PRIMARY_MERCHANT.name())) {
                throw new AccessDeniedException("Only merchant can access this resource api");
            }
        }
        if (user != null) {
            GenericSpecificationsBuilder<User> userSpecification = new GenericSpecificationsBuilder<>();

            prepareSubmerchantSearchQuery(submerchantFilterRequest, user.getId(), userSpecification);

            final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(User.class, pagedInfo);

            Page<User> pagedResult = userRepository.findAll(userSpecification.build(), pageRequest);

            List<SubMerchantDTO> content = new ArrayList<>();
            for (User usr : pagedResult.getContent()) {
                content.add(mapUserToSubMerchantDTO(usr, currentUser));
            }
            // List<SubMerchantDTO> content = pagedResult.stream().map(SUB_MERCHANT_TO_DTO).collect(Collectors.toList());
            return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), content, pagedInfo.page,
                    pagedInfo.items);
        }
        return new PageItem<>(0, 0, null, pagedInfo.page,
                pagedInfo.items);
    }

    private SubMerchantDTO mapUserToSubMerchantDTO(User user, String currentUser) {
        List<RoleDTO> allRoles = roleService.getAllRoles(currentUser);
        SubMerchantDTO merchantDTO = new SubMerchantDTO();
        merchantDTO.setId(user.getId());
        merchantDTO.setContactNumber(user.getContactNumber());
        merchantDTO.setEmail(user.getEmail());
        merchantDTO.setExtensionNumber(user.getExtensionNumber());
        merchantDTO.setCreatedAt(user.getCreatedAt());
        merchantDTO.setActive(user.isActive());
        merchantDTO.setFirstName(user.getFirstName());
        merchantDTO.setLastName(user.getLastName());
        merchantDTO.setGender(user.getGender());
//        merchantDTO.setRoleId(String.valueOf(allRoles.stream().filter(roleDTO -> roleDTO.getId().equals(user.getRoles().stream().findFirst().get().getId()))
//                .findFirst().get().getId()));
        return merchantDTO;
    }

    @Override
    public void changeSubMerchantStatus(String id, Boolean status) {
        User user = userRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new ActorPayException("Submerchant not found for given id: " + id));
        if (user != null) {
            user.setActive(status);
            user.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            userRepository.save(user);
        } else {
            throw new ObjectNotFoundException("Submerchant not found for the given id: " + id);
        }
    }

    @Override
    public MerchantOutletDTO associateMerchantToOutlet(String merchantId, String outletId) {

        Long outletCount = outletRepository.countById(outletId);
        if (outletCount == null || outletCount == 0)
            throw new RuntimeException(String.format("The given Outlet Id is not found - Outlet Id: %s", outletId));
        MerchantDetails merchantDetails = merchantDetailsRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("Merchant Id not found"));
        if (merchantDetails.getResourceType().equals(ResourceType.PRIMARY_MERCHANT)) {
            throw new RuntimeException("Since Primary Merchant owned all outlet he can't assigned explicitly");
        }
        MerchantOutlet merchantOutlet = merchantOutletRepository.findByMerchantIdAndOutletId(merchantId, outletId);

        if (merchantOutlet != null) {
            throw new RuntimeException(String.format("The given Merchant Id already associated with Outlet:" +
                    " Outlet Id: %s", merchantOutlet.getOutletId()));
        }

        // long numberOfOutlet = merchantOutletRepository.countByOutletIdAndNotMerchantId(merchantId, outletId);
        long merchantCount = merchantOutletRepository.countByMerchantIdAndNotOutletId(merchantId, outletId);
        if (merchantCount == 0) {
            merchantOutlet = new MerchantOutlet();
            merchantOutlet.setOutletId(outletId);
            merchantOutlet.setMerchantId(merchantId);
            merchantOutlet = merchantOutletRepository.save(merchantOutlet);
            MerchantOutletDTO merchantOutletDTO = new MerchantOutletDTO();
            merchantOutletDTO.setOutletId(merchantOutlet.getOutletId());
            merchantOutletDTO.setMerchantId(merchantOutlet.getMerchantId());
            merchantOutletDTO.setId(merchantOutlet.getId());
            return merchantOutletDTO;
        } else {
            throw new RuntimeException(String.format("Merchant already associated with other Outlet, MerchantID is %s: ", merchantId));
        }
    }

    @Override
    public void disassociateMerchantFromOutlet(String merchantId, String outletId) {

        Long count = merchantOutletRepository.countByMerchantIdAndOutletId(merchantId, outletId);
        if (count == null || count == 0) {
            throw new RuntimeException("Merchant and Outlet association is not found");
        }
        MerchantDetails merchantDetails = merchantDetailsRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("Merchant Id not found"));
        if (merchantDetails.getResourceType().equals(ResourceType.PRIMARY_MERCHANT)) {
            throw new RuntimeException("Primary Merchant can't Used for disassociation");
        }
        MerchantOutlet merchantOutlet = merchantOutletRepository.findByMerchantIdAndOutletId(merchantId, outletId);
        merchantOutletRepository.delete(merchantOutlet);
    }

    private void validateUserEmail(String email) {
        Optional<User> userExist = userRepository.findByEmail(email);
        if (userExist.isPresent()) {
            throw new ActorPayException(messageHelper.getMessage("email.already.exist", new Object[]{email}));
        }
    }

    private void validateUserContactNumber(String contactNumber) {
        Optional<User> user = userRepository.findUserByContactNumber(contactNumber);
        if (user.isPresent()) {
            throw new ActorPayException(messageHelper.getMessage("contact.number.already.exist", new Object[]{contactNumber}));
        }
    }

    private void prepareSubmerchantSearchQuery(SubmerchantFilterRequest filterRequest, String userId, GenericSpecificationsBuilder<User> builder) {

        builder.with(userSpecificationFactory.isEqual("deleted", false));

        if (StringUtils.isNotBlank(userId)) {
            // userId is a merchant id here
            builder.with(userSpecificationFactory.join("collaborators", "merchantId", userId));
        }

        if (StringUtils.isNotBlank(filterRequest.getFirstName())) {
            builder.with(userSpecificationFactory.like("firstName", filterRequest.getFirstName()));
        }

        if (StringUtils.isNotBlank(filterRequest.getLastName())) {
            builder.with(userSpecificationFactory.like("lastName", filterRequest.getLastName()));
        }

        if (filterRequest.getStatus() != null) {
            builder.with(userSpecificationFactory.isEqual("isActive", filterRequest.getStatus()));
        }

        if (filterRequest.getStartDate() != null) {
            builder.with(userSpecificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
        }
        if (filterRequest.getEndDate() != null) {
            builder.with(userSpecificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).atStartOfDay()));
        }

    }

    @Override
    public MerchantSubMerchantAssoc findBySubmerchantId(String submerchantId) {
        return merchantSubMerchantAssocRepository.findBySubmerchantId(submerchantId);
    }
}
