package com.octal.actorPay.controller;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.BankTransactionDTO;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.payments.BankTransferRequest;
import com.octal.actorPay.dto.payments.RazorpayPayoutRequest;
import com.octal.actorPay.dto.request.BankTransactionFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.UserBankService;
import com.octal.actorPay.service.UserService;
import com.octal.actorPay.utils.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/bank")
public class UserBankController {

    @Autowired
    private UserBankService userBankService;
    @Autowired
    private CommonService commonService;
    @Autowired
    private UserService userService;

    @PostMapping(value = "/transfer")
    public ResponseEntity<ApiResponse> transferToAnotherBank(@Valid @RequestBody RazorpayPayoutRequest payoutRequest,
                                                             HttpServletRequest request) throws Exception {
        if (Objects.isNull(payoutRequest.getUserType()) ||
                !payoutRequest.getUserType().equalsIgnoreCase(CommonConstant.USER_TYPE_MERCHANT)) {
            User user = commonService.getLoggedInUser(request);
            if (user == null || !user.isActive()) {
                throw new RuntimeException("User is not Active to transfer money");
            }

            payoutRequest.setUserId(user.getId());
            payoutRequest.setEmail(user.getEmail());
            payoutRequest.setContact(user.getContactNumber());
            payoutRequest.setName(user.getFirstName() + "  " + user.getLastName());
            payoutRequest.setAccountType("bank_account");
        }
        payoutRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
        ApiResponse apiResponse = userBankService.transferToAnotherBank(payoutRequest, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/transfer/checkout")
    public ResponseEntity<ApiResponse> transferCheckOut(@Valid @RequestBody BankTransferRequest bankTransferRequest,
                                                        HttpServletRequest request) throws Exception {
        try {
            if (Objects.isNull(bankTransferRequest.getUserType()) ||
                    !bankTransferRequest.getUserType().equalsIgnoreCase(CommonConstant.USER_TYPE_MERCHANT)) {
                User user = commonService.getLoggedInUser(request);
                if (user == null || !user.isActive()) {
                    throw new RuntimeException("User is not Active to transfer money to bank");
                }

                bankTransferRequest.setUserId(user.getId());
                bankTransferRequest.setEmail(user.getEmail());
                bankTransferRequest.setContact(user.getContactNumber());
                bankTransferRequest.setName(user.getFirstName() + "  " + user.getLastName());
                bankTransferRequest.setAccountType("bank_account");
                bankTransferRequest.setUserType(CommonConstant.USER_TYPE_CUSTOMER);
            }
            ApiResponse apiResponse = userBankService.transferCheckOut(bankTransferRequest);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), null, "101", HttpStatus.OK), HttpStatus.OK);
        }

    }

    @PostMapping(value = "/list/transactions")
    public ResponseEntity<ApiResponse> searchBankTransaction(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                             @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                             @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                             @RequestParam(name = "userType", required = false, defaultValue = "customer") String userType,
                                                             @RequestBody BankTransactionFilterRequest filterRequest,
                                                             HttpServletRequest request) throws Exception {
        if (userType != null && userType.equalsIgnoreCase(CommonConstant.USER_TYPE_CUSTOMER)) {
            User user = commonService.getLoggedInUser(request);
            filterRequest.setUserId(user.getId());
            filterRequest.setEmail(user.getEmail());
            filterRequest.setContact(user.getContactNumber());
            filterRequest.setName(user.getFirstName() + "  " + user.getLastName());
        }
        PageItem<BankTransactionDTO> transactionList =
                userBankService.searchBankTransaction(pageNo, pageSize, sortBy, asc, userType, filterRequest);
        ApiResponse apiResponse = new ApiResponse("Bank Search Result: ", transactionList, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping(value = "/list/transactions/report")
    public ResponseEntity<ApiResponse> getAllTransactionForReport(@RequestBody BankTransactionFilterRequest filterRequest) throws Exception {

        List<BankTransactionDTO> transactionList =
                userBankService.getAllTransactionForReport(filterRequest);
        ApiResponse apiResponse = new ApiResponse("Bank Search Result: ", transactionList, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
