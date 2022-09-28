package com.octal.actorPay.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.EkycStatus;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.payments.WalletDTO;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.request.DeleteRequest;
import com.octal.actorPay.dto.request.UserDeviceDetailsRequest;
import com.octal.actorPay.dto.request.UserFilterRequest;
import com.octal.actorPay.dto.request.UserRegistrationRequest;
import com.octal.actorPay.entities.*;
import com.octal.actorPay.entities.enums.SocialLoginTypeEnum;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.external.SmsService;
import com.octal.actorPay.feign.clients.MerchantClient;
import com.octal.actorPay.feign.clients.NotificationClient;
import com.octal.actorPay.feign.clients.PaymentClient;
import com.octal.actorPay.helper.MessageHelper;
import com.octal.actorPay.repositories.*;
import com.octal.actorPay.service.*;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.transformer.UserTransformer;
import com.octal.actorPay.validator.RuleValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    public static String tempOtp;
    public final String DEFAULT_PASSWORD = "Actor@123";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MerchantClient merchantClient;
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RuleValidator ruleValidator;
    @Autowired
    private UserOtpService userOtpService;
    @Autowired
    private UserOtpVerificationRepository otpVerificationRepository;
    @Autowired
    private NotificationClient notificationClient;
    @Autowired
    private CommonService commonService;
    @Autowired
    private MessageHelper messageHelper;
    @Autowired
    private UserEmailService userEmailService;
    @Autowired
    private UserOtpVerificationRepository userVerificationRepository;
    @Autowired
    private PaymentClient paymentServiceClient;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserDeviceDetailsRepository deviceDetailsRepository;

    @Autowired
    private ReferalRepository referalRepository;

    @Value(value = "${user.max.invalid.password.count}")
    private int maxInvalidPasswordCount;

    @Value("${user.invitation.link.url}")
    private String invitationLink;

    @Autowired
    private SpecificationFactory<User> userSpecificationFactory;

    @Autowired
    private UserDocumentRepository userDocumentRepository;

    @Autowired
    private UploadService uploadService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public User createUser(UserRegistrationRequest request) {
        //validate that user is present or not
        // if user is present with given phone number or email id then return with error message
        beforeCreateNewUser(request);
        User user = new User();
        Role role = roleRepository.findByName(Role.RoleName.CUSTOMER).orElseThrow(() -> new ActorPayException("Role not found"));
        // collect user roles from ROLES table
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        user.setEmail(request.getEmail());
        user.setUserType(String.valueOf(request.getUserType()));
        // map user object properties from userDto
        mapUserData(user, request);
        user.setActive(false);
        user.setPhoneVerified(Boolean.FALSE);
        user.setEmailVerified(Boolean.FALSE);
        user.setEkycStatus(EkycStatus.PENDING);
        user.setAadharNumber(request.getAadhar());
        user.setPanNumber(request.getPanNumber());
        user.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
        // save user device details
        saveUserDeviceDetails(user, request);
        //Generate Random Referral Code
        String randomReferralCode = createRandomCode(6);
        randomReferralCode = checkForGeneratedReferralCodeUser(randomReferralCode);
        user.setReferralCode(randomReferralCode);
        //Check Referral
        checkUserReferral(user, request.getReferralCode());
        User savedObject = userRepository.save(user);
        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setUserId(savedObject.getId());
        walletDTO.setActive(Boolean.TRUE);
        walletDTO.setBalanceAmount(0d);
        walletDTO.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
//        paymentServiceClient.createWallet(walletDTO);
        return savedObject;
    }

    private String checkForGeneratedReferralCodeUser(String referralCode) {
        //Check for if random referral code is exits or not.
        Optional<User> exitsReferralUser = userRepository.findByReferralCode(referralCode);
        if (!exitsReferralUser.isPresent()) {
            return referralCode;
        } else {
            String randomReferralCode = createRandomCode(6);
            return checkForGeneratedReferralCodeUser(randomReferralCode);
        }
    }


    private void beforeCreateNewUser(UserRegistrationRequest request) {
        Optional<User> userExist = userRepository.findByEmail(request.getEmail());
        if (userExist.isPresent()) {
            throw new ActorPayException(messageHelper.getMessage("email.already.exist", new Object[]{request.getEmail()}));
        }
        Optional<User> user = userRepository.findUserByContactNumber(request.getContactNumber());
        if (user.isPresent()) {
            throw new ActorPayException(messageHelper.getMessage("contact.number.already.exist", new Object[]{request.getContactNumber()}));
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


    private void saveUserDeviceDetails(User user, UserRegistrationRequest request) {
        UserDeviceDetailsRequest deviceInfo = request.getUserDeviceDetailsRequest();
        if (deviceInfo != null) {
            UserDeviceDetails userDeviceDetails = new UserDeviceDetails();
            userDeviceDetails.setAppVersion(deviceInfo.getAppVersion());
            userDeviceDetails.setDeviceData(deviceInfo.getDeviceData());
            userDeviceDetails.setDeviceToken(deviceInfo.getDeviceToken());
            userDeviceDetails.setDeviceType(deviceInfo.getDeviceType());
            userDeviceDetails.setUser(user);
            user.setUserDeviceDetails(userDeviceDetails);
        }
    }

    private void checkUserReferral(User user, String referralCode) {
        if (referralCode == null || referralCode.length() == 0) {
            System.out.println("Referral Code is Empty");
        } else {
            Optional<User> referralCodeUser = userRepository.findByReferralCode(referralCode);
            if (referralCodeUser.isPresent()) {
                user.setFromReferralCode(referralCode);
                if (referralCodeUser.get().getTotalUserRegisterWith() == null) {
                    referralCodeUser.get().setTotalUserRegisterWith(1);
                } else {
                    referralCodeUser.get().setTotalUserRegisterWith(referralCodeUser.get().getTotalUserRegisterWith() + 1);
                }
                userRepository.save(referralCodeUser.get());
            }
        }
    }

    public String createRandomCode(int codeLength) {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < codeLength; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        System.out.println(output);
        return output;
    }


    /**
     * this method is used to mapUserData user object
     *
     * @param user    - user entity
     * @param request - userDto object contains user related property
     */
    private void mapUserData(User user, UserRegistrationRequest request) {
        user.setContactNumber(request.getContactNumber());
        // user wont will be able to change their email id, will change in future release according to SRS
        //user.setEmail(userDTO.getEmail());
        user.setExtensionNumber(request.getExtensionNumber());
        //user.setUsername(userDTO.getUsername());
        user.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setGender(request.getGender());

        //TODO handle nullpointer here after changing the type of DOB
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        //user.setDateOfBirth(LocalDate.parse(userDTO.getDateOfBirth(),formatter));
        user.setActive(request.isActive());
        if (request.getDateOfBirth() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            user.setDateOfBirth(LocalDate.parse(request.getDateOfBirth(), formatter));
        }
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(user1 -> UserTransformer.USER_TO_DTO.apply(user1)).orElse(new UserDTO());
    }

    @Override
    public AuthUserDTO fetchAuthenticatedUserDetailsByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(user1 -> UserTransformer.USER_TO_AUTH_DTO.apply(user1)).orElse(null);
    }

    @Override
    public PageItem<UserDTO> getAllUsersPaged(PagedItemInfo pagedInfo, UserFilterRequest userFilterRequest) {
//        Optional<User> actor = commonService.findUserByEmail(currentUser);

//        if(actor.isPresent() && ruleValidator.isAdmin(actor.get().getId())) {
        // TODO Add admin check here
        GenericSpecificationsBuilder<User> builder = new GenericSpecificationsBuilder();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(User.class, pagedInfo);

        prepareUserSearchFilter(userFilterRequest, builder);
        Page<User> pagedResult = userRepository.findAll(builder.build(), pageRequest);
        List<UserDTO> content = pagedResult.getContent().stream().map(UserTransformer.USER_TO_DTO)
                .collect(Collectors.toList());

        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), content, pagedInfo.page,
                pagedInfo.items);
//        } else {
//            throw new RequestException(messageHelper.getMessage("admin.can.access.this.resource"));
//        }
    }

    @Override
    public User getUserByEmailId(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        // TODO, Here we will create a transformer to convert user object into dto
        if (!userOptional.isPresent())
            throw new ActorPayException("No User Found With given email");
        User user = userOptional.get();
        return user;
    }

    @Override
    public UserDTO getUserById(String userId) {
        UserDTO userDTO = null;

        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            userDTO = user.map(user1 -> UserTransformer.USER_TO_DTO.apply(user1)).orElse(null);
            //Get User Setting
            Optional<UserSetting> userSetting = settingRepository.findByUser(user.get());
            if (userSetting.isPresent()) {
                userDTO.setNotification(userSetting.get().getNotification());
            } else {
                userDTO.setNotification(false);
            }
            userDTO.setEkycVerifyStatus(user.get().getEkycStatus());
            userDTO.setAadhaarVerifyStatus(EkycStatus.PENDING);
            userDTO.setPanVerifyStatus(EkycStatus.PENDING);
            List<UserDocument> userDocuments = userDocumentRepository.findByUserAndDocTypeIn(user.get(), CommonConstant.docTypes);
            if (userDocuments != null) {
                UserDocument aadhaarDoc = userDocuments.stream().
                        filter(u -> u.getDocType().equalsIgnoreCase(CommonConstant.EKYC_AADHAR_DOC_TYPE)).findAny().orElse(null);
                UserDocument panDoc = userDocuments.stream().
                        filter(u -> u.getDocType().equalsIgnoreCase(CommonConstant.EKYC_PAN_DOC_TYPE)).findAny().orElse(null);
                if (aadhaarDoc != null)
                    userDTO.setAadhaarVerifyStatus(aadhaarDoc.getEkycStatus());
                if (panDoc != null)
                    userDTO.setPanVerifyStatus(panDoc.getEkycStatus());
            }
            if (user.get().getUserDeviceDetails() != null) {
                userDTO.setDeviceToken(user.get().getUserDeviceDetails().getDeviceToken());
                userDTO.setDeviceType(user.get().getUserDeviceDetails().getDeviceType());
            }
        } else {
            throw new ObjectNotFoundException("User not found for the given id: " + userId);
        }
        return userDTO;
    }

    @Override
    public void changeUserPassword(ChangePasswordDTO changePasswordDTO, String userName) {

        // new password and confirm password should be same
        if (changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            // get user from user id
            Optional<User> user = commonService.findUserByEmail(userName);
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
    public void updateUser(UserDTO userDTO, String currentUserEmailId, MultipartFile file) throws IOException {

        // TODO add validation that user information is updated by admin or account owner
//        Optional<User> currentUser = ruleValidator.checkPresence(userRepository.findByEmail(currentUserEmailId), "Access denied!! Invalid User found");
        // check user is present for given id or not
        Optional<User> user = ruleValidator.checkPresence(userRepository.findById(userDTO.getId()), "User not found for the give id: " + userDTO.getId());
        if (user.isPresent()) {
//            beforeUpdateUser(user.get(), currentUser.get());
            // TODO update user other information
//            mapUserData(user.get(), userDTO);

            user.get().setContactNumber(userDTO.getContactNumber());
            // user wont will be able to change their email id, will change in future release according to SRS
            //user.setEmail(userDTO.getEmail());
            user.get().setExtensionNumber(userDTO.getExtensionNumber());
            //user.setUsername(userDTO.getUsername());
            user.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
//            user.get().setGender(userDTO.getGender());
            user.get().setEmail(userDTO.getEmail());
            user.get().setUserType(userDTO.getUserType());
            //TODO handle nullpointer here after changing the type of DOB
//            user.setActive(userDTO.isActive());
            if (userDTO.getDateOfBirth() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                user.get().setDateOfBirth(LocalDate.parse(userDTO.getDateOfBirth(), formatter));
            }
            // TODO update type of DOB then uncomment it
            // user.get().setDateOfBirth(userDTO.getDateOfBirth());
            if (file != null) {
                String s3Path = uploadService.uploadFileToS3(file, "user");
                user.get().setProfilePicture(s3Path);
            }
            userRepository.save(user.get());
        } else {
            throw new ObjectNotFoundException("User not found for the give id: " + userDTO.getId());
        }
    }

    @Override
    public void resetUserPassword(String email) {
        Optional<User> user = commonService.findUserByEmail(email);
        // if user is present then send email with generated OTP
        user.ifPresent(user1 -> userOtpService.generateOtpRequestToResetUserPassword(user1, UserOtpVerification.Types.FORGOT_PASSWORD));
    }

    @Override
    public void resetUserPassword(String token, String newPassword, String confirmPassword) {
        Optional<UserOtpVerification> userVerificationToken = otpVerificationRepository.findByToken(token);

        if (userVerificationToken.isPresent()) {

            Optional<User> user = ruleValidator.checkPresence(userRepository.findById(userVerificationToken.get().getUserId()), "User not found for the give id: " + userVerificationToken.get().getUserId());

            if (user.isPresent()) {
                user.get().setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user.get());
                updateUserVerificationStatus(userVerificationToken, user.get());

            } else {
                throw new ActorPayException("Invalid Request, User object not not found");
            }
        } else {
            throw new ActorPayException("Invalid token found in request");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUserByAdmin(UserDTO userDTO, String currentUser) throws Exception {
//        Optional<User> actor = commonService.findUserByEmail(currentUser);
        // TODO add validation to check who is creating user
//        if (actor.isPresent() && ruleValidator.isAdmin(actor.get().getId())) {
        Optional<User> userExist = userRepository.findUserByEmailOrContactNumber(userDTO.getEmail(), userDTO.getContactNumber());
        if (userExist.isPresent()) {
            throw new ActorPayException(messageHelper.getMessage("email.or.phone.already.exist"));
        } else {
            User user = new User();
            Random random = new Random();
            if (userDTO.getUserType().equals("1")) {
                // find role
                Role role = roleRepository.findByName(Role.RoleName.CUSTOMER).orElseThrow(() -> new ActorPayException("Role not found"));
                // collect user roles from ROLES table
                user.setRole(role);
                // generate default password for user
                String randomPassword = String.valueOf(random.nextInt(100000));
                user.setPassword(passwordEncoder.encode(randomPassword));
                user.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
                user.setContactNumber(userDTO.getContactNumber());
                user.setEmail(userDTO.getEmail());
                user.setExtensionNumber(userDTO.getExtensionNumber());
//            user.setUsername(userDTO.getUsername());
                user.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                user.setActive(userDTO.isActive());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                user.setGender(userDTO.getGender());
                user.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
                user.setUserType(String.valueOf(userDTO.getUserType()));
                if (userDTO.getDateOfBirth() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    user.setDateOfBirth(LocalDate.parse(userDTO.getDateOfBirth(), formatter));
                }
                userRepository.save(user);
                // generated otp token
                UserOtpVerification userVerificationToken = userOtpService.generateVerificationLinkOnUserSignup(user);
                // send email notification
                userEmailService.sendEmailOnNewUserRegistrationByAdmin(user, userVerificationToken.getToken());
            }
        }
//        } else {
//            throw new RequestException(messageHelper.getMessage("admin.can.access.this.resource"));
//        }

    }

    @Override
    public void updateUserByAdmin(UserDTO userDTO) {

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(List<String> ids, String actor) throws InterruptedException {
//        Optional<User> loggedInUser = commonService.findUserByEmail(actor);
        // TODO add admin check validation
//        if (ruleValidator.isAdmin(loggedInUser.get().getId())) {
        List<User> users = userRepository.findAllById(ids);
//            userVerificationRepository.findAllByUserId()
//            List<UserOtpVerification> userVerificationRecords = users.stream().map(User::getUserOtpVerification).collect(Collectors.toList());
        //delete all the records from UserVerification table for the respected user first
        // wait for 5 milli second to finish deletion process of UserVerification then it will move to next step
//            otpVerificationRepository.deleteInBatch(userVerificationRecords);
        Thread.sleep(500);
        userRepository.deleteInBatch(users);
//        }
//        } else {
//            throw new AccessDeniedException(messageHelper.getMessage(messageHelper.getMessage("admin.can.access.this.resource")));
//        }
    }

    @Override
    public void resendOtp(String email) {

    }

    @Override
    public boolean activeNewUserByToken(String token) {
        Optional<UserOtpVerification> userVerificationToken = otpVerificationRepository.findByToken(token);

        if (userVerificationToken.isPresent()) {
            Optional<User> user = ruleValidator.checkPresence(userRepository.findById(userVerificationToken.get().getUserId()), "User not found for the give id: " + userVerificationToken.get().getUserId());

            // mark active to user account
            user.get().setActive(true);
            user.get().setEmailVerified(Boolean.TRUE);
            // update token status
            updateUserVerificationStatus(userVerificationToken, user.get());
            WalletRequest walletRequest = new WalletRequest();
            walletRequest.setUserId(user.get().getEmail());
            walletRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
            userWalletService.createCustomerWallet(walletRequest);
            return true;
        } else {
            throw new ActorPayException("Invalid request, Verification link has expired");
        }
    }

//    private void beforeUpdateUser(User user, User actor) {
//        if (!ruleValidator.canUpdateUser(user, actor)) {
//            throw new ActorPayException("Invalid request, Only account owner or admin can edit the user profile");
//        }
//    }

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
                userVerificationToken.get().setActive(false);

//                user.setUserOtpVerification(userVerificationToken.get());
                // update user object
                userRepository.save(user);
                userVerificationRepository.save(userVerificationToken.get());

            } else {
                throw new ActorPayException("Invalid request, Link has expired");
            }
        }

    }

    @Override
    public UserOtpVerification.UserVerificationStatus getUserVerificationStatus(String email) {
        Optional<User> user = ruleValidator.checkPresence(userRepository.findByEmail(email), "User not found for given email id: " + email);
        Optional<UserOtpVerification> userVerificationObj = otpVerificationRepository.findByTypeAndUserId(UserOtpVerification.Types.NEW_USER, user.get().getId());
        if (!userVerificationObj.isPresent())
            return UserOtpVerification.UserVerificationStatus.STATUS_PENDING;
        return userVerificationObj.get().getUserVerificationStatus();
    }

    @Override
    public boolean isUserAccountActive(String email) {
        Optional<User> user = commonService.findUserByEmail(email);
        return user.map(User::isActive).orElse(false);
    }

    @Override
    @Transactional
    public User userSocialSignup(SocialSignupDTO signupDTO) {
        Optional<User> userExist = userRepository.findByEmail(signupDTO.getEmail());
        // if user already exist then return existing user to allow social login
        if (userExist.isPresent()) {
            return userExist.get();//     throw new RequestException(messageHelper.getMessage("User already registered with given email id: "+signupDTO.getEmail()));
        } else {
            User user = new User();
            user.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            user.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            user.setGoogleId(signupDTO.getGoogleId());
            user.setFacebookId(signupDTO.getFacebookId());
            user.setTwitterId(signupDTO.getTwitterId());
            if (signupDTO.getLoginType() != null) {
                if (signupDTO.getLoginType().equalsIgnoreCase(SocialLoginTypeEnum.FACEBOOK.toString())) {
                    user.setSocialKey(signupDTO.getFacebookId());
                } else if (signupDTO.getLoginType().equalsIgnoreCase(SocialLoginTypeEnum.GOOGLE.toString())) {
                    user.setSocialKey(signupDTO.getGoogleId());
                } else if (signupDTO.getLoginType().equalsIgnoreCase(SocialLoginTypeEnum.TWITTER.toString())) {
                    user.setSocialKey(signupDTO.getTwitterId());
                }
            }
            user.setProfilePicture(signupDTO.getImageUrl());
            user.setEmail(signupDTO.getEmail());
            user.setExtensionNumber(signupDTO.getExtensionNumber());
            user.setContactNumber(signupDTO.getMobile());
            user.setFirstName(signupDTO.getFirstName());
            user.setLastName(signupDTO.getLastName());
            user.setActive(Boolean.TRUE);
            user.setLoginType(signupDTO.getLoginType());
            user.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
            Role role = roleRepository.findByName(Role.RoleName.CUSTOMER).orElseThrow(() -> new ActorPayException("Role not found"));
            user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
            user.setEkycStatus(EkycStatus.PENDING);
            user.setAadharNumber(signupDTO.getAadhar());
            user.setPanNumber(signupDTO.getPanNumber());
            user.setGender(signupDTO.getGender());
            if (signupDTO.getDateOfBirth() != null && signupDTO.getDateOfBirth().length() > 0) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                user.setDateOfBirth(LocalDate.parse(signupDTO.getDateOfBirth(), formatter));
            }
            user.setRole(role);
            User savedObject = userRepository.saveAndFlush(user);
            saveUserDeviceDetails(savedObject.getId(), signupDTO.getDeviceDetailsDTO());

            //Generate Random Referral Code
            String randomReferralCode = createRandomCode(6);
            randomReferralCode = checkForGeneratedReferralCodeUser(randomReferralCode);
            user.setReferralCode(randomReferralCode);
            //Check Referral
            checkUserReferral(user, signupDTO.getReferralCode());
            savedObject = userRepository.save(user);
            WalletDTO walletDTO = new WalletDTO();
            walletDTO.setUserId(savedObject.getId());
            walletDTO.setActive(Boolean.TRUE);
            walletDTO.setBalanceAmount(0d);
            walletDTO.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
            return savedObject;
        }
    }

    @Override
    @Transactional
    public User checkSocialLogin(String token) {
        Optional<User> userExist = userRepository.findBySocialKey(token);
        // if user already exist then return existing user to allow social login
        if (userExist.isPresent()) {
            return userExist.get();//     throw new RequestException(messageHelper.getMessage("User already registered with given email id: "+signupDTO.getEmail()));

        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void changeUserStatus(String id, Boolean status) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().setActive(status);
            user.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            userRepository.save(user.get());
        } else {
            throw new ObjectNotFoundException("User not found for the given id: " + id);
        }
    }

//    @Override
//    public void addMoneyToUserWallet(WalletBalanceRequest walletBalanceRequest) {
//        Optional<User> user = ruleValidator.checkPresence(userRepository.findById(addMoneyToWalletRequest.getUserId()), "User  not found for given userId: " + addMoneyToWalletRequest.getUserId());
//        if (user.isPresent()) {
//            paymentServiceClient.addMoneyToWallet(addMoneyToWalletRequest);
//        }
//
//    }

    @Override
    public Optional<CommonUserDTO> getUserDetailsByEmilOrContactNO(String emailOrContact) {
        User user = userRepository.getUserDetailsByEmilOrContactNO(emailOrContact);
        if (user != null) {
            CommonUserDTO commonUserDTO = new CommonUserDTO();
            commonUserDTO.setUserId(user.getId());
            commonUserDTO.setFirstName(user.getFirstName());
            commonUserDTO.setLastName(user.getLastName());
            commonUserDTO.setEmail(user.getEmail());
            commonUserDTO.setExtension(user.getExtensionNumber());
            commonUserDTO.setContactNumber(user.getContactNumber());
            commonUserDTO.setUserType(user.getUserType());
            commonUserDTO.setLastName(user.getLastName());
            return Optional.of(commonUserDTO);
        } else {
            try {
                ResponseEntity<ApiResponse> result = merchantClient.getUserIdentity(emailOrContact);
                if (result.getBody() != null) {
                    Gson gson = new Gson();
                    String jsonResponse = gson.toJson(result.getBody().getData());
                    CommonUserDTO merchant = gson.fromJson(jsonResponse,CommonUserDTO.class);
                    return Optional.of(merchant);
                } else {
                    return null;
                }
            }catch (Exception e){
                return null;
            }
        }
    }

    public UserDTO getUserIdentity(String userInput) {
        if (userInput.contains("+91")) {
            userInput = userInput.substring(3, userInput.length());
        }
        User user = userRepository.getUserDetailsByEmilOrContactNO(userInput);
        if (user == null) {
            throw new RuntimeException(String.format("User is not found for given Email/Mobile %s ", userInput));
        }
        UserDTO userDTO = UserTransformer.USER_TO_DTO.apply(user);
        return userDTO;
    }


//    @Override
//    public void transferMoneyByUserId(String currentUserId, String targetUserId, double amount) {
//        Optional<User> currentUser = ruleValidator.checkPresence(userRepository.findById(currentUserId), "User  not found for given userId: " + currentUserId);
//        Optional<User> targetUser = ruleValidator.checkPresence(userRepository.findById(targetUserId), "User  not found for given userId: " + targetUserId);
//        if (currentUser.isPresent()) {
//            if (targetUser.isPresent())
//                paymentServiceClient.transferMoneyByUserId(currentUserId, targetUserId, amount);
//        }
//    }

    @Override
    public void sendOTPToVerifyPhoneNumber(String loggedInUserEmail) {
        Optional<User> loggedInUser = commonService.findUserByEmail(loggedInUserEmail);
        if (loggedInUser.isPresent()) {
            UserOtpVerification userOtpVerification = userOtpService.saveUserPhoneVerificationDetails(loggedInUser.get());
            List<String> sendMsg = Stream.of(loggedInUser.get().getExtensionNumber() + loggedInUser.get().getContactNumber())
                    .collect(Collectors.toList());
            tempOtp = userOtpVerification.getOtp();
            smsService.sendSMSNotification(userOtpVerification.getOtp(), sendMsg);
        }
    }

    @Override
    public void referAndInviteLink(ReferDTO referDTO, String userName) {
        Optional<User> loggedInUser = commonService.findUserByEmail(userName);
        System.out.println("===>   " + loggedInUser.get().getEmail());
        int min = 1;
        int max = 200;
        Random random = new Random();
        String invitationToken = String.valueOf("ACTOR" + min + random.nextInt(max));

        if (loggedInUser.isPresent()) {

            Referal referalTable = new Referal();
            referalTable.setFromUserEmail(loggedInUser.get().getEmail());
            referalTable.setInviteToUserEmail(referDTO.getInviteToUserEmail());
            referalTable.setFromUserId(loggedInUser.get());
            referalTable.setReferalCode(invitationToken);
            referalTable.setActive(false);
            referalTable.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            referalTable.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            referalRepository.save(referalTable);

            Map<String, Object> model = new HashMap<>();
            model.put("Email", referDTO.getInviteToUserEmail());
            model.put("From", loggedInUser.get().getEmail());
            model.put("activationLink", invitationLink.replace("<invitation-code>", invitationToken));
            model.put("location", "India");
            model.put("sign", "ActorPay Team");

            EmailDTO mail = new EmailDTO();
            mail.setMailTo(referDTO.getInviteToUserEmail());
            mail.setSubject("[ActorPay] Invitation Link");
            mail.setTemplateName("Invitation Link");
            mail.setProps(model);
            notificationClient.sendEmailNotification(mail);
            System.out.println("DONE++++++++++");
        }
    }

    @Override
    public Boolean verifyUserContactNumberByOTP(String otp) {
        Boolean isVerified = false;
        Optional<UserOtpVerification> userVerificationObj = userVerificationRepository.findByOtp(otp);
        if (userVerificationObj.isPresent()) {
            UserOtpVerification userOtpVerification = userVerificationObj.get();
            if (userOtpVerification.getUserVerificationStatus().equals(UserOtpVerification.UserVerificationStatus.STATUS_VERIFIED)) {
                throw new ActorPayException("User Mobile is already verified");
            }
            Optional<User> user = ruleValidator.checkPresence(userRepository.findById(userVerificationObj.get().getUserId()), "No user is not associated with this otp: " + otp);
            if (!LocalDateTime.now().isBefore(userVerificationObj.get().getExpiredDateTime())) {
                throw new ActorPayException("Invalid request, OTP is expired ");
            } else {
                user.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
                user.get().setPhoneVerified(Boolean.TRUE);
                // update user verification token status
                userVerificationObj.get().setUserVerificationStatus(UserOtpVerification.UserVerificationStatus.STATUS_VERIFIED);
                userVerificationObj.get().setConfirmedDateTime(LocalDateTime.now(ZoneOffset.UTC));
                userVerificationObj.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

                // make confirmation token expired
                userVerificationObj.get()
                        .setExpiredDateTime(userVerificationObj.get().getExpiredDateTime().minusDays(1));
                userVerificationObj.get().setActive(Boolean.FALSE);

                // update user object
                userRepository.save(user.get());
                userVerificationRepository.save(userVerificationObj.get());
                isVerified = true;
            }
        }
        return isVerified;
    }

    @Override
    public void saveUserDeviceDetails(String userId, DeviceDetailsDTO deviceDetailsDTO) {
        if (Objects.nonNull(deviceDetailsDTO)) {
            UserDeviceDetails userDeviceDetails = deviceDetailsRepository.findByUserId(userId);
            if (Objects.isNull(userDeviceDetails)) {
                userDeviceDetails = new UserDeviceDetails();
                userDeviceDetails.setUser(userRepository.findById(userId).get());
            }
            userDeviceDetails.setDeviceType(deviceDetailsDTO.getDeviceType());
            userDeviceDetails.setDeviceToken(deviceDetailsDTO.getDeviceToken());
            userDeviceDetails.setDeviceData(deviceDetailsDTO.getDeviceData());
            userDeviceDetails.setAppVersion(deviceDetailsDTO.getAppVersion());
            deviceDetailsRepository.save(userDeviceDetails);
        }

    }


    @Override
    public Integer updateUserInfoOnInvalidLoginAttempts(String username, boolean isLoginSuccess) {
        Optional<User> user = ruleValidator.checkPresence(userRepository.findByEmail(username),
                messageHelper.getMessage("user.not.found"));
        // check user object is present
        if (user.isPresent()) {

            // if user logged in successfully then set user's invalid attempts 0 else update
            // user invalid password count
            if (isLoginSuccess) {
                user.get().setInvalidLoginAttempts(0);

            } else {
                // check user invalid login count, if invalid count is more than
                // maxInvalidPassword count then block user account
                // and update user invalid login count
                if (user.get().getInvalidLoginAttempts() == (maxInvalidPasswordCount - 1)) {
                    user.get().setActive(false);
                    user.get().setInvalidLoginAttempts(user.get().getInvalidLoginAttempts() + 1);
                    userRepository.save(user.get());
                    throw new ActorPayException(messageHelper.getMessage("user.account.is.blocked"));
                } else {
                    // plus one each time when user entered invalid password
                    user.get().setInvalidLoginAttempts(user.get().getInvalidLoginAttempts() + 1);
                }

            }
            // update user invalid password count and return current invalid password count
            // number
            return userRepository.save(user.get()).getInvalidLoginAttempts();

        }
        return 0;

    }


    @Override
    public GlobelSettingsResponse getUserGlobalResponse(String userName) {
        User user = userRepository.getUserDetailsByEmilOrContactNO(userName);//.orElse(null);
        UserDTO userDTO = new UserDTO();
        if (user != null) {
            userDTO.setId(user.getId());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setGender(user.getGender());
            userDTO.setDateOfBirth(user.getDateOfBirth() != null ? user.getDateOfBirth().toString() : null);
            userDTO.setEmail(user.getEmail());
            userDTO.setExtensionNumber(user.getExtensionNumber());
            userDTO.setContactNumber(user.getContactNumber());
            userDTO.setUserType(user.getUserType());
            userDTO.setLastLoginDate(user.getLastLoginDate());
            userDTO.setActive(user.isActive());
            userDTO.setEkycStatus(user.getEkycStatus());
            userDTO.setPhoneVerified(user.getPhoneVerified());
            userDTO.setEmailVerified(user.getEmailVerified());
            userDTO.setAadhar(user.getAadharNumber());
            userDTO.setPanNumber(user.getPanNumber());
            userDTO.setUserType(user.getUserType());
        } else {
            throw new ObjectNotFoundException("User not found for the userName: " + userName);
        }
        CartDTO cartDTO = cartItemService.viewCart(user.getEmail());
//
        ApiResponse apiResponse = userWalletService.getWalletBalance(user.getEmail(), CommonConstant.USER_TYPE_CUSTOMER);
        ObjectMapper mapper = new ObjectMapper();
        //read json data and convert to Wallet object
        WalletDTO walletDTO = mapper.convertValue(apiResponse.getData(), WalletDTO.class);
        walletDTO.setBalanceAmount(walletDTO.getBalanceAmount());
        System.out.println("==================   wallet  ============= === " + walletDTO.getBalanceAmount());
        return new GlobelSettingsResponse(walletDTO.getBalanceAmount(), 0, userDTO, cartDTO);
    }


    private void prepareUserSearchFilter(UserFilterRequest filterRequest, GenericSpecificationsBuilder<User> builder) {
        builder.with(userSpecificationFactory.isEqual("deleted", false));

        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getName())) {
            builder.with(userSpecificationFactory.like("firstName", filterRequest.getName())).with(userSpecificationFactory.like("lastName", filterRequest.getName()));
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getEmail())) {
            builder.with(userSpecificationFactory.isEqual("email", filterRequest.getEmail()));
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getUserType())) {
            builder.with(userSpecificationFactory.isEqual("userType", filterRequest.getUserType()));
        }
        if (filterRequest.getIsActive() != null) {
            builder.with(userSpecificationFactory.isEqual("isActive", filterRequest.getIsActive()));
        }
        if (StringUtils.isNotBlank(filterRequest.getContactNumber())) {
            builder.with(userSpecificationFactory.isEqual("contactNumber", filterRequest.getContactNumber()));
        }
        if (filterRequest.getStartDate() != null) {
            builder.with(userSpecificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
        }
        if (filterRequest.getEndDate() != null) {
            builder.with(userSpecificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).atStartOfDay()));
        }
        if (filterRequest.getEmailVerified() != null) {
            builder.with(userSpecificationFactory.isEqual("emailVerified", filterRequest.getEmailVerified()));
        }
        if (filterRequest.getPhoneVerified() != null) {
            builder.with(userSpecificationFactory.isEqual("phoneVerified", filterRequest.getPhoneVerified()));
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserById(DeleteRequest deleteRequest, String actor) {
        User user = userRepository.findById(deleteRequest.getUserId()).orElseThrow(() -> new RuntimeException("No User found with the user id : " + deleteRequest.getUserId()));
        user.setDeleted(true);
        user.setActive(false);
        user.setDeleteReason(deleteRequest.getReason());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public AdminDashboardDTO getAllUsersCount() {
        AdminDashboardDTO response = new AdminDashboardDTO();
        //Get All Users
        long totalUsers = userRepository.count();
        long totalActiveUsers = userRepository.countByIsActive(true);
        long totalInActiveUsers = userRepository.countByIsActive(false);
        response.setTotalUsers(totalUsers);
        response.setTotalActiveUsers(totalActiveUsers);
        response.setTotalInActiveUsers(totalInActiveUsers);
        return response;
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