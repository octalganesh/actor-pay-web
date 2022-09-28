package com.octal.actorPay.service.impl;

import com.octal.actorPay.client.MerchantClient;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.ContactUsDTO;
import com.octal.actorPay.dto.request.SystemConfigRequest;
import com.octal.actorPay.entities.*;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.helper.MessageHelper;
import com.octal.actorPay.repositories.ContactUsRepository;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.repositories.UserVerificationRepository;
import com.octal.actorPay.service.AdminService;
import com.octal.actorPay.service.DefaultEmailService;
import com.octal.actorPay.service.UserVerificationService;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.transformer.AdminTransformer;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.validator.RuleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactUsRepository contactUsRepository;

    @Autowired
    private RuleValidator ruleValidator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MessageHelper messageHelper;

    @Autowired
    private DefaultEmailService<User> emailService;

    @Autowired
    private UserVerificationRepository userVerificationRepository;

    @Autowired
    private UserVerificationService userVerificationService;

    @Autowired
    private MerchantClient merchantClient;

    public AdminServiceImpl() {
    }

    @Override
    public User getUserByEmailId(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        // TODO, Here we will create a transformer to convert user object into dto
        return user.orElse(null);
    }

    @Override
    public AdminDTO getUserById(String userId, String currentUser) {

        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            // TODO add proper admin validation
            if (user.get().getEmail().equals(currentUser) && user.get().getAdmin()) {
                return user.map(user1 -> AdminTransformer.ADMIN_TO_DTO.apply(user1)).orElse(null);
            } else {
                throw new ObjectNotFoundException("Only account admin and account owner can update profile");
            }
        } else {
            throw new ObjectNotFoundException("User not found for the given id: " + userId);
        }
    }

    @Override
    public void updateAdmin(AdminDTO adminDTO, String currentUserEmailId) {
        // TODO admin validation need to add
        Optional<User> currentUser = ruleValidator.checkPresence(userRepository.findByEmail(currentUserEmailId), "Access denied!! Invalid User found");
        // check user is present for given id or not
        Optional<User> user = ruleValidator.checkPresence(userRepository.findById(adminDTO.getId()), "User not found for the give id: " + adminDTO.getId());
        if (user.isPresent()) {
//            beforeUpdateUser(user.get(), currentUser.get());
            // TODO update user other information
            mapUserData(user.get(), adminDTO);

            user.get().setContactNumber(adminDTO.getContactNumber());
            user.get().setEmail(adminDTO.getEmail());
            user.get().setExtensionNumber(adminDTO.getExtensionNumber());
            user.get().setContactNumber(adminDTO.getContactNumber());
//            user.get().setIsActive(adminDTO.isActive());
//            user.get().setActive(adminDTO.getActive());
            user.get().setGender(adminDTO.getGender());
            user.get().setFirstName(adminDTO.getFirstName());
            user.get().setLastName(adminDTO.getLastName());

            // TODO update type of DOB then uncomment it
            // user.get().setDateOfBirth(userDTO.getDateOfBirth());

            userRepository.save(user.get());
        } else {
            throw new ObjectNotFoundException("User not found for the give id: " + adminDTO.getId());
        }
    }

    /**
     * this method is used to mapUserData user object
     *
     * @param user     - user entity
     * @param adminDTO - userDto object contains user related property
     */
    private void mapUserData(User user, AdminDTO adminDTO) {
        user.setContactNumber(adminDTO.getContactNumber());
        // user wont will be able to change their email id, will change in future release according to SRS
        //user.setEmail(userDTO.getEmail());
        user.setExtensionNumber(adminDTO.getExtensionNumber());
        //user.setUsername(userDTO.getUsername());
        user.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        user.setFirstName(adminDTO.getFirstName());
        user.setLastName(adminDTO.getLastName());
        user.setGender(adminDTO.getGender());

        //TODO handle nullpointer here after changing the type of DOB
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        //user.setDateOfBirth(LocalDate.parse(userDTO.getDateOfBirth(),formatter));
//        user.setIsActive(adminDTO.isActive());
//        user.setActive(adminDTO.getActive());
        if (adminDTO.getDateOfBirth() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            user.setDateOfBirth(LocalDate.parse(adminDTO.getDateOfBirth(), formatter));
        }

    }


    @Override
    public void changeAdminPassword(ChangePasswordDTO changePasswordDTO, String email) {
        // new password and confirm password should be same
        if (changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            // get user from user id
            Optional<User> user = userRepository.findByEmail(email);
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
        Optional<User> user = ruleValidator.checkPresence(userRepository.findByEmail(email), "User not found for given email id: " + email);
        Optional<UserOtpVerification> userVerificationObj = userVerificationRepository.findByTypeAndUserId(UserOtpVerification.Types.NEW_USER, user.get().getId());
        return userVerificationObj.get().getUserVerificationStatus();
    }

    @Override
    public boolean isUserAccountActive(String email) {
        Optional<User> user = ruleValidator.checkPresence(userRepository.findByEmail(email), "User not found for given email id: " + email);
        return user.map(User::getActive).orElse(false);
    }

    @Override
    public Boolean isUserAdmin(String emailId) {
        Optional<User> user = ruleValidator.checkPresence(userRepository.findByEmail(emailId), "User not found for given email id: " + emailId);
        if (user.isPresent()) {
            return user.get().getAdmin();
        }
        return false;
    }

    @Override
    public AuthUserDTO fetchAuthenticatedUserDetailsByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(user1 -> AdminTransformer.USER_TO_AUTH_DTO.apply(user1)).orElse(null);
    }

    @Override
    public void resetUserPassword(String email) {
        Optional<User> user = ruleValidator.checkPresence(userRepository.findByEmail(email), "User not found for given email id: " + email);
        // if user is present then send email with generated OTP
        user.ifPresent(user1 -> userVerificationService.generateUserOtp(user1, UserOtpVerification.Types.FORGOT_PASSWORD));
    }

    @Override
    public void resetUserPassword(String token, String newPassword, String confirmPassword) {
        Optional<UserOtpVerification> userVerificationToken = userVerificationRepository.findByToken(token);

        if (userVerificationToken.isPresent()) {

            User user = userVerificationToken.get().getUser();
            if (user != null) {
                user.setPassword(passwordEncoder.encode(newPassword));
                updateUserVerificationStatus(userVerificationToken, user);

            } else {
                throw new ActorPayException("Invalid Request, User object not not found");
            }
        }
    }


    private void updateUserVerificationStatus(Optional<UserOtpVerification> userVerificationToken, User user) {

        if (userVerificationToken.isPresent()) {
            // check verification token is expired or not, if token is expired then throw
            // exception
            if (LocalDateTime.now().isBefore(userVerificationToken.get().getExpiredDateTime())) {

                user.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                // update user verification token status
                userVerificationToken.get().setUserVerificationStatus(UserOtpVerification.UserVerificationStatus.STATUS_VERIFIED);
                userVerificationToken.get().setConfirmedDateTime(LocalDateTime.now(ZoneOffset.UTC));
                userVerificationToken.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

                // make confirmation token expired
                userVerificationToken.get()
                        .setExpiredDateTime(userVerificationToken.get().getExpiredDateTime().minusDays(2));
                // TODO, check with status only and remove active flag, need to optimize this method
//                userVerificationToken.get().setIsActive(false);
                userVerificationToken.get().setActive(false);
                user.setUserOtpVerification(userVerificationToken.get());
                // update user object
                userRepository.save(user);

            } else {
                throw new ActorPayException("Security key is expired");
            }
        }
    }

    @Override
    public AdminDTO findById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            // TODO add proper admin validation
            return user.map(user1 -> AdminTransformer.ADMIN_TO_DTO.apply(user1)).orElse(null);
        } else {
            throw new ObjectNotFoundException("User not found for the given id: " + id);
        }
    }

    @Override
    public ApiResponse updateMerchantSettingsOnGlobalSettingsUpdate(@RequestBody SystemConfigRequest systemConfigRequest, String currentLoggedEmail) {
        isUserAdmin(currentLoggedEmail);
        ResponseEntity<ApiResponse> apiResponseResponseEntity =
                merchantClient.updateMerchantSettingsOnGlobalSettingsUpdate(systemConfigRequest);
        if(apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            return apiResponseResponseEntity.getBody();
        }else{
            return new ApiResponse("Not able to update the Merchant Setting ",null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Optional<CommonUserDTO> getUserIdentity(String userIdentity) throws RuntimeException {

        User user = userRepository.getUserDetailsByEmilOrContactNO(userIdentity);
        if(user == null) {
            throw new RuntimeException(String.format("Invalid user Identity %s ",userIdentity));
        }
        if(user != null) {
            CommonUserDTO commonUserDTO = new CommonUserDTO();
            commonUserDTO.setUserId(user.getId());
            commonUserDTO.setFirstName(user.getFirstName());
            commonUserDTO.setLastName(user.getLastName());
            commonUserDTO.setEmail(user.getEmail());
            commonUserDTO.setExtension(user.getExtensionNumber());
            commonUserDTO.setContactNumber(user.getContactNumber());
            commonUserDTO.setLastName(user.getLastName());
            ;
            commonUserDTO.setUserType(user.getUserType());
            return Optional.of(commonUserDTO);
        }
        return null;
    }

    @Override
    public void saveContactUs(ContactUsDTO contactUsDTO) throws RuntimeException {

        ContactUs contact = new ContactUs();
        contact.setName(contactUsDTO.getName());
        contact.setMail(contactUsDTO.getMail());
        contact.setText(contactUsDTO.getText());
        contact.setType(contactUsDTO.getType());
        contact.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        contact.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        contact.setActive(true);
        contactUsRepository.save(contact);
    }

    @Override
    public PageItem<ContactUsDTO> getContactUsDetails(int pageNo,int pageSize,PagedItemInfo pagedInfo, String userName) {
       // final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(ContactUs.class,pagedInfo);
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ContactUs> pagedResult = contactUsRepository.findAll(pageable);


        List<ContactUsDTO> content = pagedResult.getContent().stream().map(AdminTransformer.CONTACT_US_TO_DTO)
                .collect(Collectors.toList());

        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), content, pagedInfo.page,
                pagedInfo.items);
    }
}
