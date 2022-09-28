package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.SubAdminFilterRequest;
import com.octal.actorPay.entities.Role;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserOtpVerification;
import com.octal.actorPay.exceptions.AccessDeniedException;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.helper.MessageHelper;
import com.octal.actorPay.repositories.RoleRepository;
import com.octal.actorPay.repositories.UserVerificationRepository;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.EmailService;
import com.octal.actorPay.service.SubAdminService;
import com.octal.actorPay.service.UserVerificationService;

import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.specification.UserSpecification;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.transformer.SubAdminTransformer;
import com.octal.actorPay.utils.CommonUtils;
import com.octal.actorPay.validator.RuleValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.octal.actorPay.specification.UserSpecification.*;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class SubAdminServiceImpl implements SubAdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RuleValidator ruleValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MessageHelper messageHelper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserVerificationService userVerificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserVerificationRepository userVerificationRepository;

    @Autowired
    private UserSpecification userSpecification;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SpecificationFactory<User> userSpecificationFactory;


    @Override
    public void createSubAdmin(SubAdminDTO userDTO, String currentUser) {
        Optional<User> actor = userRepository.findByEmail(currentUser);

        if (actor.isPresent() && ruleValidator.isAdmin(actor.get().getId())) {
            Optional<User> userExist = userRepository.findUserByEmailOrContactNumber(userDTO.getEmail(), userDTO.getContactNumber());
            if (userExist.isPresent()) {
                throw new ActorPayException(messageHelper.getMessage("email.or.phone.already.exist"));
            } else {
                User user = new User();
                // find role
                Optional<Role> role = roleRepository.findByNameAndIsActiveTrue(Role.RoleName.SUB_ADMIN.name());
                // collect user roles from ROLES table
                role.ifPresent(user::setRole);
                // generate default password for user
                String randomPassword = String.valueOf(CommonUtils.getRandomNumber(100000));
                user.setPassword(passwordEncoder.encode(randomPassword));
                user.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
                mapUserData(user, userDTO);
                // need to test
//                user.setIsActive(userDTO.getStatus());
                user.setActive(userDTO.getStatus());
                user.setIsAdmin(false);

                userRepository.save(user);
                // generated otp token
                UserOtpVerification userVerificationToken = userVerificationService.createUserVerificationToken(user);
                // send email notification
                emailService.sendEmailOnNewUserRegistrationByAdmin(user, userVerificationToken.getToken(),randomPassword);
            }
        } else {
            throw new ActorPayException(messageHelper.getMessage("admin.can.access.this.resource"));
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSubAdmin(SubAdminDTO userDTO, String currentUserEmailId) {
        Optional<User> currentUser = ruleValidator.checkPresence(userRepository.findByEmail(currentUserEmailId), "Access denied!! Invalid User found");
        // check user is present for given id or not
        Optional<User> user = ruleValidator.checkPresence(userRepository.findById(userDTO.getId()), "User not found for the give id: " + userDTO.getId());
        if (user.isPresent()) {
            // TODO add validation before updating profile
            if (user.get().getEmail().equals(currentUser.get().getEmail()) || currentUser.get().getAdmin()) {
//            beforeUpdateUser(user.get(), currentUser.get());
                // TODO update user other information
                user.get().setActive(userDTO.getStatus());
                mapUserData(user.get(), userDTO);
            userRepository.save(user.get());
        } else {
                throw new ObjectNotFoundException("Only account admin or  account owner can update profile");
            }
        } else {
            throw new ObjectNotFoundException("User not found for the given id: " + userDTO.getId());
        }
    }

    /**
     * this method is used to mapUserData user object
     *
     * @param user    - user entity
     * @param userDTO - userDto object contains user related property
     */
    private void mapUserData(User user, SubAdminDTO userDTO) {
        user.setContactNumber(userDTO.getContactNumber());
        user.setExtensionNumber(userDTO.getExtensionNumber());
        user.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setGender(userDTO.getGender());
        user.setAddress(userDTO.getAddress());
        if (userDTO.getDateOfBirth() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            user.setDateOfBirth(LocalDate.parse(userDTO.getDateOfBirth(), formatter));
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSubAdmin(List<String> ids, String currentUser) throws InterruptedException {
        Optional<User> loggedInUser = userRepository.findByEmail(currentUser);
        if (ruleValidator.isAdmin(loggedInUser.get().getId())) {
            List<User> users = userRepository.findAllById(ids);
            List<UserOtpVerification> userVerificationRecords = users.stream().map(User::getUserOtpVerification).collect(Collectors.toList());
            //delete all the records from UserVerification table for the respected user first
            // wait for 5 milli second to finish deletion process of UserVerification then it will move to next step
            userVerificationRepository.deleteInBatch(userVerificationRecords);
            Thread.sleep(500);
            userRepository.deleteInBatch(users);
        } else {
            throw new AccessDeniedException(messageHelper.getMessage(messageHelper.getMessage("admin.can.access.this.resource")));
        }
    }

    @Override
    public SubAdminDTO getSubAdminInfo(String userId, String currentUser) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            return user.map(user1 -> SubAdminTransformer.SUB_ADMIN_TO_DTO.apply(user1)).orElse(null);
        } else {
            throw new ObjectNotFoundException("User not found for the given id: " + userId);
        }
    }

    //
    @Override
    public PageItem<SubAdminDTO> getAllSubAdminPaged(PagedItemInfo pagedInfo, String currentUser ,SubAdminFilterRequest subAdminFilterRequest) {
        Optional<User> actor = userRepository.findByEmail(currentUser);

        if(actor.isPresent() && ruleValidator.isAdmin(actor.get().getId())) {
            final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(User.class, pagedInfo);
            GenericSpecificationsBuilder<User> builder = new GenericSpecificationsBuilder<>();

            prepareSubAdminSearchFilter(subAdminFilterRequest, builder);
            Page<User> pagedResult = userRepository.findAll(builder.build(), pageRequest);
            List<SubAdminDTO> content = pagedResult.getContent().stream().map(SubAdminTransformer.SUB_ADMIN_TO_DTO)
                    .collect(Collectors.toList());

            return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), content, pagedInfo.page,
                    pagedInfo.items);
        } else {
            throw new ActorPayException(messageHelper.getMessage("admin.can.access.this.resource"));
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeSubAdminPassword(ChangePasswordDTO changePasswordDTO, String userName) {
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
    public void changeSubAdminStatus(String id, Boolean status) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().setActive(status);
            user.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            userRepository.save(user.get());
        } else {
            throw new ObjectNotFoundException("Sub admin not found for the given id: " + id);
        }
    }



    @Override
    public List<User> findUserWithKey(UserDTO pUser) {
        System.out.println(userRepository.findAll(where(getEmail(pUser.getEmail()))));
        return userRepository.findAll(where(getFirstName(pUser.getFirstName())
                .or(getLastName(pUser.getLastName()))
                .or(getContactNumber(pUser.getContactNumber()))
                .or(getEmail(pUser.getEmail()))
                .and(getIsActive(pUser.isActive()))
                .and(getCreatedAt(pUser.getCreatedAt())
                )));
    }

    @Override
    public Map<String, Object> findUserWithPaginationAndSorting(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> allPage = userRepository.findAll(pageable);

        Map<String, Object> pageResponse = new HashMap<>();
        pageResponse.put("currentPageNo", allPage.getNumber());
        pageResponse.put("totalRecordsInDB", allPage.getTotalElements());
        pageResponse.put("totalPages", allPage.getTotalPages());

        return pageResponse;
    }

    private void prepareSubAdminSearchFilter(SubAdminFilterRequest filterRequest, GenericSpecificationsBuilder<User> builder) {

        builder.with(userSpecificationFactory.isEqual("deleted", false));
        builder.with(userSpecificationFactory.isEqual("isAdmin", false));
        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getEmail())) {
            builder.with(userSpecificationFactory.isEqual("email", filterRequest.getEmail()));
        }
        if (filterRequest.getIsActive() != null) {
            builder.with(userSpecificationFactory.isEqual("isActive", filterRequest.getIsActive()));
        }
        if (StringUtils.isNotBlank(filterRequest.getName())) {
            builder.with(userSpecificationFactory.isEqual("firstName", filterRequest.getName()));
        }
        if (StringUtils.isNotBlank(filterRequest.getContactNumber())) {
            builder.with(userSpecificationFactory.isEqual("contactNumber", filterRequest.getContactNumber()));
        }
        if (filterRequest.getStartDate()!= null) {
            builder.with(userSpecificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
        }
        if (filterRequest.getEndDate() != null) {
            builder.with(userSpecificationFactory.isLessThan("createdAt", filterRequest.getEndDate()
                    .plusDays(1).atStartOfDay()));
        }
//        if (filterRequest.getEmailVerified() != null) {
//            builder.with(userSpecificationFactory.isLessThanOrEquals("emailVerified", filterRequest.getEmailVerified()));
//        }
//        if (filterRequest.getPhoneVerified() != null) {
//            builder.with(userSpecificationFactory.isLessThanOrEquals("phoneVerified", filterRequest.getPhoneVerified()));
//        }
    }

}