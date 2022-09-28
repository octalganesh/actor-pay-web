package com.octal.actorPay.service;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.DeleteRequest;
import com.octal.actorPay.dto.request.UserFilterRequest;
import com.octal.actorPay.dto.request.UserRegistrationRequest;
import com.octal.actorPay.entities.UserOtpVerification;
import com.octal.actorPay.entities.User;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author naveen kumawat
 */

@Component
public interface UserService {

    /**
     * create new user
     * @param request - DTO contains all the required properties which is needed to create a new user
     * @return - returns persisted object once user is created
     */
    User createUser(UserRegistrationRequest request);

    /**
     * this method is used to get user by username
     * @param username - registered username
     * @return - return UserDTO object that contains user information
     */
    UserDTO getUserByUsername(String username);

    // authentication purpose only
    AuthUserDTO fetchAuthenticatedUserDetailsByEmail(String email);

    /**
     * get all the user list
     * @param pagedInfo - contains pagination parameters
     * @return - returns pageItem User dto that contains list of users
     */
    PageItem<UserDTO> getAllUsersPaged(PagedItemInfo pagedInfo, UserFilterRequest userFilterRequest);

    /**
     * Get User details by user email id
     * @param email - registered user email id
     * @return - return user object
     */
    User getUserByEmailId(String email);

    /**
     * Get User details by user id
     * @param userId - registered user id
     * @return - return user object
     */
    UserDTO getUserById(String userId);

    /**
     * it will change the existing user password
     * @param changePasswordDTO - this dto object contains all the required properties to change user password
     * @param userId - User Id
     */
    void changeUserPassword(ChangePasswordDTO changePasswordDTO, String userId);


    /**
     * Update existing user information
     * @param userDTO - contains user properties
     * @param currentUserEmailId - loggedIn user email id
     */
    void updateUser(UserDTO userDTO, String currentUserEmailId, MultipartFile file) throws IOException;


    /**
     * this method is used to send email on give email id with OTP, if email is not already registered then it throws error
     * @param email - email will be sent on this email with unique OTP
     */
    void resetUserPassword(String email);

    /**
     * this method is used to reset user password, user has to enter OTP received in email with password
     * @param token - a unique number received over email
     * @param newPassword - user new password
     * @param confirmPassword - confirm password should be same as new password
     */
    void resetUserPassword(String token, String newPassword, String confirmPassword);

    /**
     * this method provides functionality to add new user by admin ,
     *
     * @param userDTO - contains user properties
     */
    void addUserByAdmin(UserDTO userDTO, String currentUser) throws Exception;

    void updateUserByAdmin(UserDTO userDTO);

    /**
     * delete users by id
     * @param ids - list of user ids
     * @param actor - current logged in user's username
     */
    void deleteUser(List<String> ids, String actor) throws InterruptedException;

    void resendOtp(String email);

    boolean activeNewUserByToken(String token);

    UserOtpVerification.UserVerificationStatus getUserVerificationStatus(String email);

    public boolean isUserAccountActive(String email);

    User userSocialSignup(SocialSignupDTO signupDTO);

    User checkSocialLogin(String token);

    void changeUserStatus(String id, Boolean status);

    Optional<CommonUserDTO> getUserDetailsByEmilOrContactNO(String emailOrContact);

    void sendOTPToVerifyPhoneNumber(String loggedInUserEmail);

    Boolean verifyUserContactNumberByOTP(String otp);

    void saveUserDeviceDetails(String userId, DeviceDetailsDTO deviceDetailsDTO);

    Integer updateUserInfoOnInvalidLoginAttempts(String username, boolean isLoginSuccess);

    UserDTO getUserIdentity(String userInput);

    Object getUserGlobalResponse(String userName);

    void referAndInviteLink(ReferDTO referTableDTO, String userName);

    void deleteUserById(DeleteRequest deleteRequest, String userName);
    AdminDashboardDTO getAllUsersCount();

    UserNotificationListDTO getUserNotificationList(User loggedInUser,UserNotificationListDTO.ListRequest request) throws Exception;

}