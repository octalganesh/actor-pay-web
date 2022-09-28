package com.octal.actorPay.service;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.ContactUsDTO;
import com.octal.actorPay.dto.request.SystemConfigRequest;
import com.octal.actorPay.entities.ContactUs;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserOtpVerification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

@Service
public interface AdminService {

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
    AdminDTO getUserById(String userId,String currentUser);


    /**
     * Update existing user information
     * @param adminDTO - contains user properties
     * @param currentUserEmailId - loggedIn user email id
     */
    void updateAdmin(AdminDTO adminDTO, String currentUserEmailId);

    /**
     * it will change the existing user password
     * @param changePasswordDTO - this dto object contains all the required properties to change user password
     * @param userId - User Id
     */
    void changeAdminPassword(ChangePasswordDTO changePasswordDTO, String userId);

    UserOtpVerification.UserVerificationStatus getUserVerificationStatus(String email);

    public boolean isUserAccountActive(String email);

    Boolean isUserAdmin(String emailId);

    // authentication purpose only
    AuthUserDTO fetchAuthenticatedUserDetailsByEmail(String email);

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

    AdminDTO findById(String id);

    ApiResponse updateMerchantSettingsOnGlobalSettingsUpdate(@RequestBody SystemConfigRequest systemConfigRequest, String currentLoggedEmail);

    Optional<CommonUserDTO> getUserIdentity(@Param("userIdentity") String userIdentity) throws RuntimeException;

    void saveContactUs(@RequestBody ContactUsDTO contactUsDTO) throws RuntimeException;

    PageItem<ContactUsDTO> getContactUsDetails(int pageNo,int pageSize,PagedItemInfo pagedInfo,String userName);
}
