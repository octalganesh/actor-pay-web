package com.octal.actorPay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.client.AdminFeignClient;
import com.octal.actorPay.client.PaymentWalletClient;
import com.octal.actorPay.client.UserServiceClient;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.ResourceType;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.*;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.entities.UserDocument;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.exceptions.ExceptionUtils;
import com.octal.actorPay.listners.events.RegistrationCompleteEvent;
import com.octal.actorPay.service.MerchantService;
import com.octal.actorPay.service.SubMerchantService;
import com.octal.actorPay.service.UserDocumentService;
import com.octal.actorPay.service.impl.MerchantServiceImpl;
import com.octal.actorPay.utils.QRCodeGenerator;
import com.octal.actorPay.utils.ResponseUtils;
import feign.FeignException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

import static org.springframework.http.ResponseEntity.ok;

@Controller
public class MerchantController extends BaseController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private SubMerchantService subMerchantService;

    @Autowired
    private AdminFeignClient adminFeignClient;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private QRCodeGenerator qrCodeGenerator;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private PaymentWalletClient paymentWalletClient;

    @Autowired
    private UserDocumentService userDocumentService;


    @GetMapping(value = "/merchant/auth/details/by/email/{email}")
    public ResponseEntity getUserByUserName(@PathVariable("email") String email) {
        return ok(merchantService.fetchAuthenticatedUserDetailsByEmail(email));
    }

    @PostMapping("/merchant/signup")
    public ResponseEntity<?> createUser(@RequestBody @Valid MerchantDTO merchantDTO, final HttpServletRequest request) throws Exception {
        try {
            merchantDTO.setResourceType(ResourceType.PRIMARY_MERCHANT);
//            merchantDTO.setRoleId("95bdc80e-d475-4a43-9a96-d08a061f866b");
            User registered = merchantService.create(merchantDTO, request.getHeader("userName"));
            eventPublisher.publishEvent(new RegistrationCompleteEvent(registered, Locale.ENGLISH, getAppUrl(request)));
            return new ResponseEntity<>(new ApiResponse("Your account created successfully. Please check your mail to activate account.", null,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } catch (FeignException fe) {
            ApiResponse errorMessage = ExceptionUtils.parseFeignExceptionMessage(fe);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    //    @Secured("ROLE_MERCHANT_UPDATE")
    @PutMapping("/merchant/update")
    public ResponseEntity<?> updateUser(@RequestPart(value = "merchantDTO") MerchantDTO merchantDTO,
            @RequestPart(value = "file", required = false) MultipartFile file,
            final HttpServletRequest request, @RequestParam(value = "user" +
            "Type", required = false, defaultValue = "merchant") String userType) {
        try {
            merchantService.update(merchantDTO, request.getHeader("userName"), userType,file);
        } catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                DataIntegrityViolationException dataIntegrityViolationException = (DataIntegrityViolationException) e;
                String errorMessage = ExceptionUtils.duplicateCheckHandleException(dataIntegrityViolationException);
                throw new RuntimeException(errorMessage);
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
        return new ResponseEntity<>(new ApiResponse("Merchant data updated successfully.", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/merchant/update/settings")
    public ResponseEntity<?> updateMerchantSettingsOnGlobalSettingsUpdate(@RequestBody SystemConfigRequest systemConfigRequest) {
        merchantService.updateMerchantSettingByMerchantType(systemConfigRequest.getParamName(),
                systemConfigRequest.getParamValue(), systemConfigRequest.getMerchantType());
        return new ResponseEntity<>(new
                ApiResponse("Merchant Settings updated Successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    //    @Secured("ROLE_MERCHANT_VIEW_BY_ID")
    @GetMapping(value = "/merchant/by/id/{id}")
    public ResponseEntity getUserById(@PathVariable("id") String id, final HttpServletRequest request) {
//        return ok(merchantService.getMerchantDetails(id, request.getHeader("userName")));
        User user = getAuthorizedUser(request);
        return new ResponseEntity<>(new ApiResponse("Merchant Details: ",
                merchantService.getMerchantDetails(id, user.getId()),
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/merchant/by/merchantId/{merchantId}")
    public ResponseEntity<ApiResponse> getMerchantByMerchantId(@PathVariable(name = "merchantId") String merchantId) {
        MerchantDTO merchantDTO = merchantService.getMerchantDetailsByMerchantId(merchantId);
        if (merchantDTO == null) {
            return new ResponseEntity<>(new ApiResponse(String.format("Merchant not found for given Merchant Id %s", merchantId),
                    null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse(String.format("Merchant Details ", ""),
                    merchantDTO, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }

    }

    @GetMapping(value = "/merchants/by/userId/{userId}")
    public ResponseEntity<ApiResponse> getMerchantByUserId(@PathVariable("userId") String userId) {
        MerchantDTO merchantDTO = merchantService.getMerchantByUserId(userId);
        if (merchantDTO == null) {
            throw new RuntimeException(String.format("Invalid userId %s ", userId));
        }
        return new ResponseEntity<ApiResponse>(new ApiResponse("Merchant Details ",
                merchantDTO, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    //    @Secured("ROLE_MERCHANT_LIST_VIEW")
    @PostMapping(value = "/merchants/get/all/paged")
    public ResponseEntity<ApiResponse> getAllUsersPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                        @RequestParam(defaultValue = "createdAt") String sortBy,
                                                        @RequestParam(defaultValue = "false") boolean asc,
                                                        @RequestParam(defaultValue = "merchant", required = false) String userType,
                                                        @RequestBody MerchantFilterRequest filterRequest,
                                                        HttpServletRequest request) {
        String currentUser = request.getHeader("userName");
        if (StringUtils.isNotBlank(currentUser) && CommonConstant.USER_TYPE_MERCHANT.equalsIgnoreCase(userType)) {
            filterRequest.setEmail(currentUser);
        }

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<MerchantDTO> merchantDTOList = merchantService.getAllMerchantsPaged(pagedInfo, filterRequest);
        ApiResponse apiResponse = new ApiResponse("Merchant Details", merchantDTOList, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }

    //    @Secured("ROLE_MERCHANT_VIEW")
    @GetMapping(value = "/merchant/get/all")
    public ResponseEntity<ApiResponse> getAllMerchant(@RequestParam(name = "sortBy", defaultValue = "businessName", required = false) String sortBy,
                                                      @RequestParam(name = "asc", required = false, defaultValue = "false") Boolean asc,
                                                      @RequestParam(name = "filterByIsActive", defaultValue = "true", required = false)
                                                      Boolean filterByIsActive) {
        return new ResponseEntity<>(new ApiResponse("All Users fetched Successfully.",
                merchantService.getAllMerchant(filterByIsActive, sortBy, asc),
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }


    /**
     * change user account password
     *
     * @param passwordDTO - password request
     * @param request     - object of HttpServletRequest
     * @return - message
     */

//    @Secured("ROLE_MERCHANT_CHANGE_PASSWORD")
    @PostMapping(value = "/merchant/user/change/password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordDTO passwordDTO,
                                                      HttpServletRequest request) {
        merchantService.changeMerchantPassword(passwordDTO, request.getHeader("userName"));
        return new ResponseEntity<>(new ApiResponse("Password successfully changed", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    //    @Secured("ROLE_MERCHANT_FORGOT_PASSWORD")
    @PostMapping("/merchant/forget/password")
    public ResponseEntity<?> forgetUserPassword(@RequestBody Map<String, String> data) {
        if (!data.isEmpty()) {
            merchantService.resetUserPassword(data.get("emailId"));
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

//    @Secured("ROLE_MERCHANT_RESET_PASSWORD")
    @PostMapping("/merchant/reset/password")
    public ResponseEntity<?> resetUserPassword(@RequestBody ChangePasswordDTO passwordDTO,
                                               HttpServletRequest request) {
        merchantService.resetUserPassword(passwordDTO.getToken(), passwordDTO.getNewPassword(), passwordDTO.getConfirmPassword());
        return new ResponseEntity<>(new ApiResponse("Password reset successfully", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    //  @Secured("ROLE_MERCHANT_LIST_ACTIVE_CATEGORIES")
    @GetMapping(value = "/get/all/active/categories")
    public ResponseEntity getAllActiveCategories() {
        return new ResponseEntity<>(adminFeignClient.getAllActiveCategories(), HttpStatus.OK);
    }

    @Secured("ROLE_MERCHANT_LIST_CATEGORIES")
    @GetMapping(value = "/get/all/categories/paged")
    public ResponseEntity getAllCategoriesPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                @RequestParam(defaultValue = "createdAt") String sortBy,
                                                @RequestParam(defaultValue = "false") boolean asc,
                                                @RequestParam(name = "filterByName", required = false) String filterByName,
                                                @RequestParam(name = "filterByIsActive", required = false) Boolean filterByIsActive) {
        return new ResponseEntity<>(adminFeignClient.getAllCategoriesPaged(pageNo, pageSize, sortBy, asc, filterByName, filterByIsActive), HttpStatus.OK);
    }

    //    @Secured("ROLE_MERCHANT_LIST_SUBCATEGORIES")
    @GetMapping(value = "/get/all/subcategories/paged")
    public ResponseEntity getAllSubCategoriesPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestParam(defaultValue = "createdAt") String sortBy,
                                                   @RequestParam(defaultValue = "false") boolean asc,
                                                   @RequestParam(name = "filterByName", required = false) String filterByName,
                                                   @RequestParam(name = "filterByIsActive", required = false) Boolean filterByIsActive,
                                                   @RequestParam(name = "filterByCategoryName", required = false) String filterByCategoryName) {
        return new ResponseEntity<>(adminFeignClient.getAllSubCategoriesPaged(pageNo, pageSize, sortBy, asc, filterByName, filterByIsActive, filterByCategoryName), HttpStatus.OK);
    }

//    @GetMapping(value = "/get/all/subcategories/by/category")
//    public ResponseEntity getAllActiveSubCategories(@RequestParam("categoryId") String categoryId) {
//        return new ResponseEntity<>(adminFeignClient.getAllActiveSubCategories(categoryId), HttpStatus.OK);
//    }

    //    @Secured("ROLE_MERCHANT_PHONE_VERIFY")
    @PostMapping(value = "/phone/verify")
    public ResponseEntity<ApiResponse> verifyUserOtp(@RequestParam(name = "otp") String otp) {
        Boolean isVerified = merchantService.verifyUserContactNumberByOTP(otp);
        if (isVerified) {
            return new ResponseEntity<>(new ApiResponse("User contact number has been verified successfully", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("User contact number is not verified", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }
    }

    //    @Secured("ROLE_MERCHANT_SEND_OTP_REQUEST")
    @GetMapping(value = "/phone/otp/request")
    public ResponseEntity<ApiResponse> sendOtpOnPhone(HttpServletRequest request) {
        String loggedInUser = request.getHeader("userName");
        merchantService.sendOTPToVerifyPhoneNumber(loggedInUser);
        return new ResponseEntity<>(new ApiResponse("OTP sent successfully", MerchantServiceImpl.tempOtp, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    //    @Secured("ROLE_MERCHANT_RESEND_ACTIVATION_LINK")
    @PostMapping("/resend/activation/link/request")
    public ResponseEntity<?> resendEmailActivationLink(@RequestBody Map<String, String> map, final HttpServletRequest request) {
        String emailId = map.get("emailId");

        if (StringUtils.isBlank(emailId)) {
            return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Invalid request, email id must not be blank", null, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }

        User user = merchantService.getUserByEmailId(emailId);
        if (user == null) {
            return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Invalid request, User not found for given email id: " + emailId, null, HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
        }
        eventPublisher.publishEvent(new RegistrationCompleteEvent(user, Locale.ENGLISH, getAppUrl(request)));
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Email sent, Please check your mail to activate your account.", null, HttpStatus.OK), HttpStatus.OK);
    }

    //    @Secured("ROLE_MERCHANT_VIEW_NAME")
    @GetMapping("/merchantName/{merchantId}")
    public ResponseEntity<String> getMerchantName(@PathVariable("merchantId") String merchantId) {
        return new ResponseEntity<String>(merchantService.findMerchantName(merchantId), HttpStatus.OK);
    }

    //    @Secured("ROLE_MERCHANT_VIEW_BY_BUSINESS")
    @GetMapping("/merchantBusinessName/{businessName}")
    public ResponseEntity<ApiResponse> findByMerchantBusinessName(@PathVariable("businessName") String businessName) {
        MerchantDTO merchantDTO = merchantService.findByMerchantBusinessName(businessName);
        if (merchantDTO != null) {
            return new ResponseEntity<>(new ApiResponse("Merchant Details",
                    merchantDTO.getMerchantId(), String.valueOf(HttpStatus.NOT_FOUND.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Invalid Merchant Id",
                    merchantDTO, String.valueOf(HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
        }
    }

    //    @Secured("ROLE_MERCHANT_VIEW_UPI_QRCODE")
    @GetMapping("/merchant/qrcode")
    public ResponseEntity<ApiResponse> getMerchantUPIQRCode(HttpServletRequest request) throws Exception {
        String email = request.getHeader("userName");
        MerchantQRDTO merchantQRDTO = merchantService.processQRCode(email);
        return new ResponseEntity<ApiResponse>(new ApiResponse("QR Code: ", merchantQRDTO,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/{userInput}/get")
    public ResponseEntity<ApiResponse> getUserByEmailOrMobile(@PathVariable("userInput") String userInput) {
        UserDTO userDTO = merchantService.findUserByEmailOrContactNumber(userInput);
        return new ResponseEntity<>(new ApiResponse("User Details: ", userDTO, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/get/user/{userIdentity}")
    public ResponseEntity<ApiResponse> getUserIdentity(@PathVariable("userIdentity") String userIdentity) {
        System.out.println("getUserIdentity");
        CommonUserDTO commonUserDTO = merchantService.findUserIdentity(userIdentity).orElseThrow(() -> new RuntimeException("User not found"));
        return new ResponseEntity<ApiResponse>(new ApiResponse("User Details ",
                commonUserDTO, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @GetMapping(value = "/get/customer/{customerIdentity}")
    public ResponseEntity<ApiResponse> getCustomerIdentity(@PathVariable("customerIdentity") String userIdentity) {
        ResponseEntity<ApiResponse> responseEntity = userServiceClient.getCustomerIdentity(userIdentity);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            ApiResponse apiResponse = responseEntity.getBody();
            ObjectMapper mapper = new ObjectMapper();
            CommonUserDTO commonUserDTO = mapper.convertValue(apiResponse.getData(), CommonUserDTO.class);
            return new ResponseEntity<ApiResponse>(new ApiResponse("Customer Details ",
                    commonUserDTO, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<ApiResponse>(new ApiResponse("Customer Details not found",
                    null, String.valueOf(HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/users/get/{userIdentity}")
    public ResponseEntity<ApiResponse> findUserType(@PathVariable("userIdentity") String userIdentity) {
        ResponseEntity<ApiResponse> responseEntity = paymentWalletClient.findUserType(userIdentity);
        return responseEntity;
    }

    @PostMapping("/ekyc/verify")
    public ResponseEntity<ApiResponse> verifyKyc(@RequestPart(value = "front_part", required = false) MultipartFile front_part,
                                                 @RequestPart(value = "back_part", required = false) MultipartFile back_part,
                                                 HttpServletRequest request) throws Exception {
        User user = Optional.of(merchantService.getUserByEmailId(request.getHeader("userName")))
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
        ekycResponse.setUserId(userId);
        return new ResponseEntity<ApiResponse>(new ApiResponse("Document Details ",
                ekycResponse, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/ekyc/update")
    public ResponseEntity<ApiResponse> updateEkycStatus(@RequestBody EkycUpdateRequest updateRequest) throws Exception {
        UserDocument userDocument = userDocumentService.updateEkycStatus(updateRequest);
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
        ekycResponse.setUserId(userDocument.getUser().getId());
        return new ResponseEntity<ApiResponse>(new ApiResponse("Document Details ",
                ekycResponse, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

/*    @PostMapping("/ekyc/get/by/key")
    public ResponseEntity<ApiResponse> getEkycByKey(@RequestBody EkycFilterRequest ekycFilterRequest) throws Exception {
        List<UserDocument> allEkyc = userDocumentService.getAllEkyc();

        List<EkycFilterRequest> list = new ArrayList<>();
        for (UserDocument userDocument : allEkyc) {
            EkycFilterRequest documentData = MerchantTransformer.MERCHANT_EKYC_TO_DTO.apply(userDocument);
            list.add(documentData);
        }
        return new ResponseEntity<ApiResponse>(new ApiResponse("Merchant Ekyc Details ",
                list, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);

       *//* UserDocument userDocument = userDocumentService.getAllEkyc();
        EkycResponse ekycResponse = new ObjectMapper().readValue( userDocument.getDocumentData(), EkycResponse.class);
        ekycResponse.setCreatedAt(userDocument.getCreatedAt().toString());
        ekycResponse.setUpdatedAt(userDocument.getUpdatedAt() != null ? userDocument.getUpdatedAt().toString() : null);*//*


    }*/

    @PostMapping("/ekyc/get/by/key")
    public ResponseEntity<ApiResponse> getEkycByKey(@RequestParam(defaultValue = "0") Integer pageNo,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestParam(defaultValue = "createdAt") String sortBy,
                                                    @RequestParam(defaultValue = "false") boolean asc,
                                                    @RequestBody EkycFilterRequest ekycFilterRequest) throws Exception {

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<EkycFilterRequest> allMerchantsEkyc = userDocumentService.getAllEkyc(pagedInfo, ekycFilterRequest);
        ApiResponse apiResponse = new ApiResponse("Merchant Ekyc Details", allMerchantsEkyc, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);

    }
    @GetMapping("/merchants/getAllMerchantsCount")
    public ResponseEntity<ApiResponse> getAllMerchantsCount() {
        return new ResponseEntity<>(new ApiResponse("All Merchants Counts.", merchantService.getAllMerchantsCount(), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/notification-list")
    public ResponseEntity<?> notificationList(@RequestHeader(value = "userName") String userName,@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                              @RequestParam(defaultValue = "createdAt") String sortBy,
                                              @RequestParam(defaultValue = "false") boolean asc, @RequestBody UserNotificationListDTO.ListRequest request) {
        try {
            User loggedInUser = merchantService.getUserByEmailId(userName);
            if (loggedInUser != null) {
                request.setPageNo(pageNo);
                request.setPageSize(pageSize);
                request.setSortBy(sortBy);
                request.setAsc(asc);
                return new ResponseEntity<>(new ApiResponse("User Notification List", merchantService.getUserNotificationList(loggedInUser, request),
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
        return adminFeignClient.saveContactUs(contactUsDTO);
       // return new ResponseEntity<>(new ApiResponse("Thanks for your enquiry", adminFeignClient.saveContactUs(contactUsDTO), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

}

