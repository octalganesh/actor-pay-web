package com.octal.actorPay.controller;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.*;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.UserPGService;
import com.octal.actorPay.service.UserWalletService;
import com.octal.actorPay.utils.CommonService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class UserPGController {

    @Autowired
    private UserWalletService userWalletService;

    @Autowired
    private UserPGService userPGService;

    @Autowired
    private CommonService commonService;

    public UserPGController(UserWalletService userWalletService) {
        this.userWalletService = userWalletService;
    }

    @PostMapping("/v1/pg/qrcode/create")
    public ResponseEntity<ApiResponse> createQrCode(@RequestBody QrCodeCreateRequest qrCodeRequest, HttpServletRequest request) {
        User user = commonService.getLoggedInUser(request);
        qrCodeRequest.setUserId(user.getId());
        ApiResponse apiResponse = userPGService.createQrCode(qrCodeRequest);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @GetMapping("/v1/pg/payment/{paymentTypeId}")
    ResponseEntity<ApiResponse> getPGPaymentDetails(@PathVariable("paymentTypeId") String paymentTypeId) {
        ApiResponse apiResponse = userPGService.getPGPaymentDetails(paymentTypeId);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @PostMapping("/v1/pg/fundaccount/create")
    ResponseEntity<ApiResponse> createFundAccount(@Valid @RequestBody BankAccountRequest addBankRequest, HttpServletRequest request) {
        User user = commonService.getLoggedInUser(request);
        addBankRequest.setUserId(user.getId());
        addBankRequest.setEmail(user.getEmail());
        addBankRequest.setContact(user.getContactNumber());
        addBankRequest.setName(user.getFirstName() + "  " + user.getLastName());
        addBankRequest.setAccountType("bank_account");
        addBankRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
        ApiResponse apiResponse = userPGService.createFundAccount(addBankRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/v1/pg/contacts/create")
    ResponseEntity<ApiResponse> createContact(@RequestBody ContactRequest contactRequest) {
        ApiResponse apiResponse = userPGService.createContact(contactRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/v1/pg/fundaccount")
    public ResponseEntity<ApiResponse> getUserFundAccounts(HttpServletRequest request) {
        User user = commonService.getLoggedInUser(request);
        ApiResponse apiResponse = userPGService.getUserFundAccounts(user.getId());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/v1/pg/fundaccount/self")
    public ResponseEntity<ApiResponse> getUsersSelfFundAccounts(HttpServletRequest request) {
        User user = commonService.getLoggedInUser(request);
        ApiResponse apiResponse = userPGService.getUsersSelfFundAccounts(user.getId());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/v1/pg/fundaccount/beneficiary")
    public ResponseEntity<ApiResponse> getUsersBeneficiaryAccounts(HttpServletRequest request) {
        User user = commonService.getLoggedInUser(request);
        ApiResponse apiResponse = userPGService.getUsersBeneficiaryAccounts(user.getId());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/v1/pg/fundaccount/{fundAccountId}")
    public ResponseEntity<ApiResponse> getUserFundAccountByFundAccountId(@PathVariable("fundAccountId") String fundAccountId,
                                                                   HttpServletRequest request) {
        User user = commonService.getLoggedInUser(request);
        ApiResponse apiResponse = userPGService.getUserFundAccountByFundAccountId(user.getId(),fundAccountId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @PutMapping("/v1/pg/fundaccount/activeordeactive")
    public ResponseEntity<ApiResponse> activeOrDeActiveAccount(@RequestBody DeactivateRequest deactivateRequest,
                                                               HttpServletRequest request) throws RazorpayException {
        User user = commonService.getLoggedInUser(request);
        deactivateRequest.setUserId(user.getId());
        deactivateRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
        ApiResponse apiResponse = userPGService.activeOrDeActiveAccount(deactivateRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/v1/pg/fundaccount/update")
    public ResponseEntity<ApiResponse>setPrimaryOrSecondaryAccount(@RequestBody UpdateFundAccountRequest fundAccountRequest,
                                                          HttpServletRequest request) throws RazorpayException {
        User user = commonService.getLoggedInUser(request);
        fundAccountRequest.setUserId(user.getId());
        fundAccountRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
        ApiResponse apiResponse = userPGService.setPrimaryOrSecondaryAccount(fundAccountRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/v1/pg/fundaccount/update-self")
    public ResponseEntity<ApiResponse> setSelfAccount(@RequestBody UpdateFundAccountRequest fundAccountRequest,
                                            HttpServletRequest request) throws RazorpayException {
        User user = commonService.getLoggedInUser(request);
        fundAccountRequest.setUserId(user.getId());
        fundAccountRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
        ApiResponse apiResponse = userPGService.setSelfAccount(fundAccountRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
