package com.octal.actorPay.service;

import com.octal.actorPay.constants.MerchantType;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.payments.OrderRequest;
import com.octal.actorPay.dto.request.MerchantFilterRequest;
import com.octal.actorPay.entities.MerchantDetails;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserOtpVerification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public interface MerchantService {


    // authentication purpose only
    AuthUserDTO fetchAuthenticatedUserDetailsByEmail(String email);

    /**
     * Get User details by user email id
     * @param email - registered user email id
     * @return - return user object
     */
    User getUserByEmailId(String email);

    void saveMerchantDeviceDetails(String userId, DeviceDetailsDTO deviceDetailsDTO);

    User create(MerchantDTO userDTO, String currentUser) throws Exception;

    void update(MerchantDTO userDTO, String currentUser, String userType, MultipartFile file) throws IOException;

    void delete(List<String> ids, String currentUser) throws InterruptedException;

    MerchantDTO getMerchantDetails(String id, String currentUser);
    MerchantDTO getMerchantDetailsByMerchantId(String merchantId);

    /**
     * get all the sub admin list
     * @param pagedInfo - contains pagination parameters
     * @return - returns pageItem Sub admin dto that contains list of sub admins
     */
    PageItem<MerchantDTO> getAllMerchantsPaged(PagedItemInfo pagedInfo, MerchantFilterRequest filterRequest);
    PageItem<MerchantDTO> getAllSubMerchantsByMerchant(PagedItemInfo pagedInfo, MerchantFilterRequest filterRequest,String loggedInMerchant) throws Exception;

    void changeMerchantPassword(ChangePasswordDTO changePasswordDTO, String userId);

    UserOtpVerification.UserVerificationStatus getUserVerificationStatus(String email);

    boolean isUserAccountActive(String email);

    boolean activeNewUserByToken(String token);

    void resetUserPassword(String email);

    /**
     * this method is used to reset user password, user has to enter OTP received in email with password
     * @param token - a unique number received over email
     * @param newPassword - user new password
     * @param confirmPassword - confirm password should be same as new password
     */
    void resetUserPassword(String token, String newPassword, String confirmPassword);

    MerchantDetails getMerchantIdByEmail(String email);

//    Optional<Collaborators> getSubMerchantById(String userId);

    Boolean verifyUserContactNumberByOTP(String otp);
    void sendOTPToVerifyPhoneNumber(String loggedInUserEmail);
    String findMerchantName(String merchantId);

    MerchantDTO findByMerchantBusinessName(String merchantName);

    List<MerchantDTO> getAllMerchant(Boolean filterByIsActive,String sortBy,boolean asc);

    void updateMerchantSettingByMerchantType(String paramName,String paramValue,MerchantType merchantType);

    MerchantQRDTO processQRCode(String email) throws Exception;

    UserDTO findUserByEmailOrContactNumber(String userIdentity);

    Optional<CommonUserDTO> findUserIdentity(String userIdentity);

    MerchantDTO getMerchantByUserId(String userId);

//    MerchantResponse findMerchantBasicData(String userId);

    MerchantResponse findMerchantBasicData(String email);

    User findByPrimaryMerchantBySubMerchantId(String subMerchantId);

//    Long findAadhaarCount(String email,String aadharNumber);
//    Long findPanCount(String email, String panNumber);

    ApiResponse checkout(OrderRequest orderRequest);

    long getAllMerchantsCount();

    UserNotificationListDTO getUserNotificationList(User loggedInUser,UserNotificationListDTO.ListRequest request) throws Exception;

}
