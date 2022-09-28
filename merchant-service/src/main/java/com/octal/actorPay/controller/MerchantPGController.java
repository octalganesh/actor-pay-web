package com.octal.actorPay.controller;

import com.octal.actorPay.client.PaymentWalletClient;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.BankAccountRequest;
import com.octal.actorPay.dto.payments.ContactRequest;
import com.octal.actorPay.dto.payments.DeactivateRequest;
import com.octal.actorPay.dto.payments.UpdateFundAccountRequest;
import com.octal.actorPay.entities.User;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class MerchantPGController extends BaseController {

    @Autowired
    private PaymentWalletClient paymentWalletClient;

    @Secured("ROLE_FUND_ACCOUNT_CREATE")
    @PostMapping("/v1/pg/fundaccount/create")
    ResponseEntity<ApiResponse> createFundAccount(@Valid @RequestBody BankAccountRequest addBankRequest, HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        addBankRequest.setUserId(user.getId());
        addBankRequest.setEmail(user.getEmail());
        addBankRequest.setContact(user.getContactNumber());
        addBankRequest.setName(user.getFirstName() + "  " + user.getLastName());
        addBankRequest.setAccountType("bank_account");
        addBankRequest.setUserType(CommonConstant.USER_TYPE_MERCHANT);
        ApiResponse apiResponse = paymentWalletClient.createFundAccount(addBankRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Secured("ROLE_FUND_CONTACT_CREATE")
    @PostMapping("/v1/pg/contacts/create")
    ResponseEntity<ApiResponse> createContact(@RequestBody ContactRequest contactRequest) {
        ApiResponse apiResponse = paymentWalletClient.createContact(contactRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Secured("ROLE_FUND_ACCOUNT_LIST_VIEW")
    @GetMapping("/v1/pg/fundaccount")
    public ResponseEntity<ApiResponse> getUsersFundAccounts(HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        ApiResponse apiResponse = paymentWalletClient.getUserFundAccounts(user.getId());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Secured("ROLE_FUND_ACCOUNT_VIEW_BY_ID")
    @GetMapping("/v1/pg/fundaccount/{fundAccountId}")
    public ResponseEntity<ApiResponse> getUserFundAccountByFundAccountId(@PathVariable("fundAccountId") String fundAccountId,
                                                                         HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        ApiResponse apiResponse = paymentWalletClient.getUserFundAccountByFundAccountId(user.getId(),fundAccountId);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Secured("ROLE_FUND_ACCOUNT_STATUS")
    @PutMapping("/v1/pg/fundaccount/activeordeactive")
    public ResponseEntity<ApiResponse> activeOrDeActiveAccount(@RequestBody DeactivateRequest deactivateRequest,
                                                               HttpServletRequest request) throws RazorpayException {
        User user = getAuthorizedUser(request);
        deactivateRequest.setUserId(user.getId());
        deactivateRequest.setUserType(CommonConstant.USER_TYPE_MERCHANT);
        ApiResponse apiResponse = paymentWalletClient.activeOrDeActiveAccount(deactivateRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Secured("ROLE_FUND_ACCOUNT_UPDATE")
    @PutMapping("/v1/pg/fundaccount/update")
    public ResponseEntity<ApiResponse>setPrimaryOrSecondaryAccount(@RequestBody UpdateFundAccountRequest fundAccountRequest,
                                                                   HttpServletRequest request) throws RazorpayException {
        User user = getAuthorizedUser(request);
        fundAccountRequest.setUserId(user.getId());
        fundAccountRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
        ApiResponse apiResponse = paymentWalletClient.setPrimaryOrSecondaryAccount(fundAccountRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/v1/pg/fundaccount/update-self")
    public ResponseEntity<ApiResponse> setSelfAccount(@RequestBody UpdateFundAccountRequest fundAccountRequest,
                                                      HttpServletRequest request) throws RazorpayException {
        User user = getAuthorizedUser(request);
        fundAccountRequest.setUserId(user.getId());
        fundAccountRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
        ApiResponse apiResponse = paymentWalletClient.setSelfAccount(fundAccountRequest);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/v1/pg/fundaccount/self")
    public ResponseEntity<ApiResponse> getUsersSelfFundAccounts(HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        ApiResponse apiResponse = paymentWalletClient.getUsersSelfFundAccounts(user.getId());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Secured("ROLE_BENEFICIARY_ACCOUNT_LIST_BY_USER")
    @GetMapping("/v1/pg/fundaccount/beneficiary")
    public ResponseEntity<ApiResponse> getUsersBeneficiaryAccounts(HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        ApiResponse apiResponse = paymentWalletClient.getUsersBeneficiaryAccounts(user.getId());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
