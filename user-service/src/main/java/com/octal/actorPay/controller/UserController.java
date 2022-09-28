package com.octal.actorPay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.*;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserDocument;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.feign.clients.AdminClient;
import com.octal.actorPay.feign.clients.MerchantClient;
import com.octal.actorPay.feign.clients.PaymentClient;
import com.octal.actorPay.jwt.JwtTokenProvider;
import com.octal.actorPay.listners.events.RegistrationCompleteEvent;
import com.octal.actorPay.service.UserDocumentService;
import com.octal.actorPay.service.UserService;
import com.octal.actorPay.service.UserWalletService;
import com.octal.actorPay.service.impl.UserServiceImpl;
import com.octal.actorPay.utils.ResponseUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/users")
public class UserController extends PagedItemsController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AdminClient adminClient;

    @Autowired
    private PaymentClient paymentServiceClient;

    @Autowired
    private MerchantClient merchantClient;

    @Autowired
    private UserDocumentService userDocumentService;

    @GetMapping(value = "/auth/details/by/email/{email}")
    public ResponseEntity getUserByUserName(@PathVariable("email") String email) {
        return ok(userService.fetchAuthenticatedUserDetailsByEmail(email));
    }

    @GetMapping(value = "/by/id/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable("id") String id) {

        UserDTO userDTO = userService.getUserById(id);
        return new ResponseEntity<>(new ApiResponse("User details fetched successfully", userDTO,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserRegistrationRequest registrationRequest, final HttpServletRequest request) throws Exception {
        User registered = userService.createUser(registrationRequest);
        if (registered != null) {
            eventPublisher.publishEvent(new RegistrationCompleteEvent(registered, Locale.ENGLISH, getAppUrl(request)));
            return new ResponseEntity<>(new ApiResponse("Your account created successfully. Please check your mail to activate account.", null,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }

        return new ResponseEntity<>("Something went wrong, User creation failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping(value = "/get/user/{username}")
    public ResponseEntity getUser(@PathVariable("username") String username) {
        return ok(userService.getUserByUsername(username));
    }


    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @GetMapping("/getAllUsersCount")
    public ResponseEntity<ApiResponse> getAllUsersCount() {
        return new ResponseEntity<>(new ApiResponse("All Users Counts.", userService.getAllUsersCount(), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }


    @PostMapping(value = "/get/all/user/paged")
    public ResponseEntity getAllUsersPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestParam(defaultValue = "createdAt") String sortBy,
                                           @RequestParam(defaultValue = "asc") boolean asc,
                                           @RequestParam(defaultValue = "customer", required = false) String userType,
                                           @RequestBody UserFilterRequest userFilterRequest,
                                           HttpServletRequest request) {

     /*   String currentUser = request.getHeader("userName");
        if(StringUtils.isNotBlank(currentUser) && CommonConstant.USER_TYPE_CUSTOMER.equals(userType))  {
            userFilterRequest.setEmail(currentUser);
        }*/
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);

        PageItem<UserDTO> userDTOList = userService.getAllUsersPaged(pagedInfo, userFilterRequest);
        ApiResponse apiResponse = new ApiResponse("All Users fetched Successfully", userDTOList, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);

       /* Object data = apiResponse.getData();
        JSONObject jsonObject = new JSONObject((Map<String, String>) data);
        Number totalItems = jsonObject.getAsNumber("totalItems");*/

        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }

    /**
     * change user account password
     *
     * @param passwordDTO - password request
     * @param request     - object of HttpServletRequest
     * @return - message
     */
    @PostMapping(value = "/change/password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordDTO passwordDTO,
                                                      HttpServletRequest request) {
        userService.changeUserPassword(passwordDTO, request.getHeader("userName"));
        return new ResponseEntity<>(new ApiResponse("Password successfully changed", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value = "/refer/invite/link")
    public ResponseEntity<ApiResponse> referAndInviteLink(@Valid @RequestBody ReferDTO referTableDTO, HttpServletRequest httpServletRequest) {

        userService.referAndInviteLink(referTableDTO, httpServletRequest.getHeader("userName"));
        return new ResponseEntity<>(new ApiResponse("Invitation Link send to " + referTableDTO.getInviteToUserEmail().toUpperCase() + ", Please use code to get discount.",
                null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }


    /**
     * update user information
     *
     * @param userDTO    - userDTO object that contains user information
     * @param request - HttpServletRequest Object
     */
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestPart(value = "userDTO") UserDTO userDTO,
                                    @RequestPart(value = "file", required = false) MultipartFile file, HttpServletRequest request) throws IOException {
        userService.updateUser(userDTO, request.getHeader("userName"),file);

        return new ResponseEntity<>(new ApiResponse("User updated successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    /**
     * if user is forget their current password then this api is used to reset password
     * it will send a email to user's given email id with auto generated OTP
     *
     * @return
     */
    @PostMapping("/forget/password")
    public ResponseEntity<?> forgetUserPassword(@RequestBody Map<String, String> data) {
        if (!data.isEmpty()) {
            userService.resetUserPassword(data.get("emailId"));
            return new ResponseEntity<>(new ApiResponse("Please check your mail to reset your password.", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Invalid request,email id not found in request", null, String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * once a user received a email with auto generated otp then user has to enter otp with new password
     *
     * @return
     */
    @PostMapping("/reset/password")
    public ResponseEntity<?> resetUserPassword(@RequestBody ChangePasswordDTO passwordDTO,
                                               HttpServletRequest request) {
        userService.resetUserPassword(passwordDTO.getToken(), passwordDTO.getNewPassword(), passwordDTO.getConfirmPassword());
        return new ResponseEntity<>(new ApiResponse("Password reset successfully", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }


    @DeleteMapping("/delete/by/ids")
    public ResponseEntity<?> deleteUserById(@RequestBody Map<String, List<String>> userIds, HttpServletRequest request) throws InterruptedException {
        userService.deleteUser(userIds.get("userIds"), request.getHeader("userName"));
        return new ResponseEntity<>(new ApiResponse("User deleted successfully", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/delete/by/userId")
    public ResponseEntity<?> deleteUserById(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        userService.deleteUserById(deleteRequest, request.getHeader("userName"));
        return new ResponseEntity<>(new ApiResponse("User deleted successfully", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    /**
     * if user is forget their current password then this api is used to reset password
     * it will send a email to user's given email id with auto generated OTP
     *
     * @param email - registered email id of user
     * @return
     */
    @PostMapping("/resend/otp")
    public ResponseEntity<?> resendOtp(@RequestParam("email") String email) {
        userService.resetUserPassword(email);
        return new ResponseEntity<>(new ApiResponse("OTP sent successfully", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/add/new/user")
    public ResponseEntity<ApiResponse> createUserByAdmin(@RequestBody @Valid UserDTO userDTO, final HttpServletRequest request) {
        try {
            userService.addUserByAdmin(userDTO, request.getHeader("userName"));
            return new ResponseEntity<>(new ApiResponse("User added successfully", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null, String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.OK), HttpStatus.OK);
        }
    }

    @PostMapping("/social/signup")
    public ResponseEntity<?> userSocialSignup(@RequestBody @Valid SocialSignupDTO signupDTO, final HttpServletRequest request) {
        User registeredUser = userService.userSocialSignup(signupDTO);
        if (registeredUser != null) {
            AuthenticationResponse authenticationResponse = jwtTokenProvider.generateToken(registeredUser);
            return new ResponseEntity<>(new ApiResponse("User login successfully", authenticationResponse, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }

        return new ResponseEntity<>("Something went wrong, User signup failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/social/check/{token}")
    public ResponseEntity<?> checkSocialLogin(@PathVariable String token) {
        User registeredUser = userService.checkSocialLogin(token);
        if (registeredUser != null) {
            AuthenticationResponse authenticationResponse = jwtTokenProvider.generateToken(registeredUser);
            return new ResponseEntity<>(new ApiResponse("User login successfully", authenticationResponse, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("User not found with given social Id", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }
    }

    @PutMapping("/change/status")
    public ResponseEntity<ApiResponse> changeUserStatus(@RequestParam(name = "id") String id, @RequestParam(name = "status") Boolean status) throws InterruptedException {
        userService.changeUserStatus(id, status);
        return new ResponseEntity<>(new ApiResponse("User status updated successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }


    @GetMapping(value = "/phone/otp/request")
    public ResponseEntity<ApiResponse> sendOtpOnPhone(HttpServletRequest request) {
        String loggedInUser = request.getHeader("userName");
        userService.sendOTPToVerifyPhoneNumber(loggedInUser);
        return new ResponseEntity<>(new ApiResponse("OTP sent successfully", UserServiceImpl.tempOtp, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value = "/phone/verify")
    public ResponseEntity<ApiResponse> verifyUserOtp(@RequestParam(name = "otp") String otp) {
        Boolean isVerified = userService.verifyUserContactNumberByOTP(otp);
        if (isVerified) {
            return new ResponseEntity<>(new ApiResponse("User contact number has been verified successfully", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("User contact number is not verified", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }
    }

    @PostMapping("/resend/activation/link/request")
    public ResponseEntity<?> resendEmailActivationLink(@RequestBody Map<String, String> map, final HttpServletRequest request) {
        String emailId = map.get("emailId");

        if (StringUtils.isBlank(emailId)) {
            return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Invalid request, email id must not be blank", null, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }

        User user = userService.getUserByEmailId(emailId);
        if (user == null) {
            return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Invalid request, User not found for given email id: " + emailId, null, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
        }
        eventPublisher.publishEvent(new RegistrationCompleteEvent(user, Locale.ENGLISH, getAppUrl(request)));
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Email sent, Please check your mail to activate your account.", null, HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/{userInput}/get")
    public ResponseEntity<ApiResponse> getUserByEmailOrMobile(@PathVariable("userInput") String userInput) {
        try {
            UserDTO userDTO = userService.getUserIdentity(userInput);
            return new ResponseEntity<>(new ApiResponse("User Details: ", userDTO, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }
    }

    @GetMapping(value = "/get/global/response")
    public ResponseEntity<ApiResponse> getUserGlobalResponse(HttpServletRequest request) throws ObjectNotFoundException, IOException {
        String userName = request.getHeader("userName");
        Object globelSettingsResponse = userService.getUserGlobalResponse(userName);
        return new ResponseEntity<>(new ApiResponse("User Global response", globelSettingsResponse, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/get/user/identity/{userIdentity}")
    public ResponseEntity<ApiResponse> getUserIdentity(@PathVariable("userIdentity") String userIdentity) {
        try {
            Optional<CommonUserDTO> commonUserDTO = userService.getUserDetailsByEmilOrContactNO(userIdentity);
            if (commonUserDTO == null || !commonUserDTO.isPresent()) {
                return new ResponseEntity<ApiResponse>(new ApiResponse("User not found",
                        null, "101", HttpStatus.OK), HttpStatus.OK);
            }
            return new ResponseEntity<ApiResponse>(new ApiResponse("User Details ",
                    commonUserDTO, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(e.getMessage(),
                    null, "101", HttpStatus.OK), HttpStatus.OK);
        }
    }

    @GetMapping(value = "/get/merchant/{merchantIdentity}")
    public ResponseEntity<ApiResponse> getMerchantIdentity(@PathVariable("merchantIdentity") String userIdentity) {
        ResponseEntity<ApiResponse> responseEntity = merchantClient.getUserIdentity(userIdentity);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = responseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            CommonUserDTO commonUserDTO = mapper.convertValue(apiResponse.getData(), CommonUserDTO.class);
            return new ResponseEntity<ApiResponse>(new ApiResponse("Merchant Details ",
                    commonUserDTO, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<ApiResponse>(new ApiResponse("Merchant Details not found",
                    null, String.valueOf(HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get/{userIdentity}")
    public ResponseEntity<ApiResponse> findUserType(@PathVariable("userIdentity") String userIdentity) {
        try {
            ResponseEntity<ApiResponse> responseEntity = paymentServiceClient.findUserType(userIdentity);
            return responseEntity;
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null, String.valueOf(101), HttpStatus.OK), HttpStatus.OK);
        }
    }

    @PostMapping("/ekyc/verify")
    public ResponseEntity<ApiResponse> verifyKyc(@RequestPart(value = "front_part", required = false) MultipartFile front_part,
                                                 @RequestPart(value = "back_part", required = false) MultipartFile back_part,
                                                 HttpServletRequest request) throws Exception {
        User user = Optional.of(userService.getUserByEmailId(request.getHeader("userName")))
                .orElseThrow(() -> new ActorPayException("User not found"));
        EkycUserResponse response = userDocumentService.verify(front_part, back_part, user);
        return new ResponseEntity<ApiResponse>(new ApiResponse("Verify Details ",
                response, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/ekyc/{userId}")
    public ResponseEntity<ApiResponse> getKYCByUser(@PathVariable("userId") String userId) throws Exception {
        List<UserDocument> userDocumentList = userDocumentService.findByUser(userId);
        return new ResponseEntity<ApiResponse>(new ApiResponse("eKyc Status ",
                userDocumentList, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/ekyc/{userId}/{docType}")
    public ResponseEntity<ApiResponse> getKYCByDocType(@PathVariable(name = "docType") String docType,
                                                       @PathVariable("userId") String userId,
                                                       HttpServletRequest request) throws Exception {
        UserDocument userDocument = userDocumentService.getKYCByDocType(userId, docType);
        EkycResponse ekycResponse = new ObjectMapper().readValue(userDocument.getDocumentData(), EkycResponse.class);
        ekycResponse.setCreatedAt(userDocument.getCreatedAt().toString());
        ekycResponse.setUpdatedAt(userDocument.getUpdatedAt() != null ? userDocument.getUpdatedAt().toString() : null);
//        ekycResponse.setEkycVerifyStatus(userDocument.getEkycStatus());
        ekycResponse.setReasonDescription(userDocument.getReasonDescription());
        if (userDocument != null && userDocument.getDocType().equalsIgnoreCase(CommonConstant.EKYC_AADHAR_DOC_TYPE)) {
            ekycResponse.setAadhaarVerifyStatus(userDocument.getEkycStatus());
        }
        if (userDocument != null && userDocument.getDocType().equalsIgnoreCase(CommonConstant.EKYC_PAN_DOC_TYPE)) {
            ekycResponse.setPanVerifyStatus(userDocument.getEkycStatus());
        }
        ekycResponse.setReasonDescription(userDocument.getReasonDescription());
        return new ResponseEntity<ApiResponse>(new ApiResponse("Document Details ",
                ekycResponse, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/ekyc/update")
    public ResponseEntity<ApiResponse> updateEkycStatus(@RequestBody EkycUpdateRequest updateRequest) throws Exception {

        UserDocumentDTO userDocumentDTO = userDocumentService.updateEkycStatus(updateRequest);
        EkycResponse ekycResponse = new ObjectMapper().readValue(userDocumentDTO.getDocumentData(), EkycResponse.class);
        ekycResponse.setCreatedAt(userDocumentDTO.getCreatedAt().toString());
        ekycResponse.setUpdatedAt(userDocumentDTO.getUpdatedAt() != null ? userDocumentDTO.getUpdatedAt().toString() : null);
        ekycResponse.setEkycVerifyStatus(userDocumentDTO.getEkycStatus());
        ekycResponse.setReasonDescription(userDocumentDTO.getReasonDescription());
        if (userDocumentDTO != null && userDocumentDTO.getDocType().equalsIgnoreCase(CommonConstant.EKYC_AADHAR_DOC_TYPE)) {
            ekycResponse.setAadhaarVerifyStatus(userDocumentDTO.getEkycStatus());
        }
        if (userDocumentDTO != null && userDocumentDTO.getDocType().equalsIgnoreCase(CommonConstant.EKYC_PAN_DOC_TYPE)) {
            ekycResponse.setPanVerifyStatus(userDocumentDTO.getEkycStatus());
        }
        ekycResponse.setUserId(userDocumentDTO.getId());
        return new ResponseEntity<ApiResponse>(new ApiResponse("Document Details ",
                ekycResponse, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/ekyc/get/by/key")
    public ResponseEntity<ApiResponse> getEkycByKey(@RequestParam(defaultValue = "0") Integer pageNo,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestParam(defaultValue = "createdAt") String sortBy,
                                                    @RequestParam(defaultValue = "false") boolean asc,
                                                    @RequestBody EkycFilterRequest ekycFilterRequest) throws Exception {

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<EkycFilterRequest> allMerchantsEkyc = userDocumentService.getAllEkyc(pagedInfo, ekycFilterRequest);
        ApiResponse apiResponse = new ApiResponse("Customer Ekyc Details", allMerchantsEkyc, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);

    }

    @PostMapping("/notification-list")
    public ResponseEntity<?> notificationList(@RequestHeader(value = "userName") String userName,@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                              @RequestParam(defaultValue = "createdAt") String sortBy,
                                              @RequestParam(defaultValue = "false") boolean asc, @RequestBody UserNotificationListDTO.ListRequest request) {
        try {
            User loggedInUser = userService.getUserByEmailId(userName);
            if (loggedInUser != null) {
                request.setPageNo(pageNo);
                request.setPageSize(pageSize);
                request.setSortBy(sortBy);
                request.setAsc(asc);
                return new ResponseEntity<>(new ApiResponse("User Notification List", userService.getUserNotificationList(loggedInUser, request),
                        String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
            } else if (loggedInUser == null) {
                return new ResponseEntity<>(new ApiResponse("No User Found with give email.", null,
                        String.valueOf(101), HttpStatus.OK), HttpStatus.OK);
            }
        } catch (ActorPayException notFoundException) {
            return new ResponseEntity<>(new ApiResponse(notFoundException.getMessage(), null,
                    String.valueOf(101), HttpStatus.OK), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null,
                    String.valueOf(101), HttpStatus.OK), HttpStatus.OK);
        }
        return new ResponseEntity<>("Something went wrong, User creation failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/contact-us")
    public ResponseEntity<ApiResponse> saveContactUs(@RequestBody ContactUsDTO contactUsDTO){
        //  return new ResponseEntity<ApiResponse>((MultiValueMap<String, String>) adminClient.saveContactUs(contactUsDTO), HttpStatus.OK);
        return adminClient.saveContactUs(contactUsDTO);
    }




}