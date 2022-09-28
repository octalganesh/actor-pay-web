package com.octal.actorPay.controller;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.BankTransferRequest;
import com.octal.actorPay.dto.payments.RazorpayPayoutRequest;
import com.octal.actorPay.dto.request.BankTransactionFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.MerchantBankService;
import com.octal.actorPay.utils.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/bank")
public class MerchantBankController extends BaseController{
    @Autowired
    private MerchantBankService userBankService;
    @Autowired
    private CommonService commonService;

    @Secured("ROLE_BANK_TRANSFER")
    @PostMapping(value = "/transfer")
    public ResponseEntity<ApiResponse> transferToAnotherBank(@Valid @RequestBody RazorpayPayoutRequest payoutRequest,
                                                             HttpServletRequest request) throws Exception {
        User user = getAuthorizedUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to transfer money");
        }

        payoutRequest.setUserId(user.getId());
        payoutRequest.setEmail(user.getEmail());
        payoutRequest.setContact(user.getContactNumber());
        payoutRequest.setName(user.getFirstName() + "  " + user.getLastName());
        payoutRequest.setAccountType("bank_account");
        payoutRequest.setUserType(CommonConstant.USER_TYPE_MERCHANT);
        return userBankService.transferToAnotherBank(payoutRequest);
    }

    @Secured("ROLE_BANK_CHECKOUT")
    @PostMapping(value = "/transfer/checkout")
    public ResponseEntity<ApiResponse> transferCheckOut(@Valid @RequestBody BankTransferRequest bankTransferRequest,
                                                        HttpServletRequest request) throws Exception {
        User user = getAuthorizedUser(request);
        if (user == null || !user.isActive()) {
            throw new RuntimeException("User is not Active to transfer money to bank");
        }

        bankTransferRequest.setUserId(user.getId());
        bankTransferRequest.setEmail(user.getEmail());
        bankTransferRequest.setContact(user.getContactNumber());
        bankTransferRequest.setName(user.getFirstName() + "  " + user.getLastName());
        bankTransferRequest.setAccountType("bank_account");
        bankTransferRequest.setUserType(CommonConstant.USER_TYPE_MERCHANT);
        return userBankService.transferCheckOut(bankTransferRequest);
    }

    @Secured("ROLE_BANK_TRANSACTIONS_LIST_VIEW")
    @PostMapping(value = "/list/transactions")
    public ResponseEntity<ApiResponse> searchBankTransaction(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                             @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                             @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                             @RequestParam(name = "userType", required = false, defaultValue = "customer") String userType,
                                                             @RequestBody BankTransactionFilterRequest filterRequest,
                                                             HttpServletRequest request) throws Exception {
        filterRequest.setUserType(CommonConstant.USER_TYPE_MERCHANT);
        User user = getAuthorizedUser(request);
        if (user == null) {
            throw new RuntimeException("User is not Active to Request Transaction");
        }
        filterRequest.setUserId(user.getId());
        filterRequest.setEmail(user.getEmail());
        filterRequest.setContact(user.getContactNumber());
        filterRequest.setName(user.getFirstName() + "  " + user.getLastName());
        return userBankService.searchBankTransaction(pageNo, pageSize, sortBy, asc, userType, filterRequest);
    }
}
