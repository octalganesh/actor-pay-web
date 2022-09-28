package com.octal.actorPay.service;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.ContactRequest;
import com.octal.actorPay.dto.payments.RefundRequest;
import com.octal.actorPay.dto.payments.WalletRequest;
import com.octal.actorPay.dto.request.RequestMoneyFilter;
import com.octal.actorPay.dto.request.WalletFilterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AdminPaymentService {


    ApiResponse searchWalletTransaction(Integer pageNo,Integer pageSize,String sortBy,boolean asc,String userType,
                                        WalletFilterRequest filterRequest);
    ApiResponse getWalletBalance(String userId);
    ApiResponse getRequestMoney(RequestMoneyFilter requestMoneyFilter, Integer pageNo, Integer pageSize,
                                       String sortBy, boolean asc);

    ApiResponse addMoneyToWallet(WalletRequest walletRequest) throws Exception;

    ApiResponse transferMoney(WalletRequest walletRequest) throws Exception;

    ApiResponse withdraw(WalletRequest withdrawRequest) throws Exception;

    ApiResponse createContact(ContactRequest contactRequest) throws Exception;

    ApiResponse doRefund(RefundRequest refundRequest);

    ApiResponse searchPayrollDetails(Integer pageNo, Integer pageSize, String sortBy, boolean asc, String userType, WalletFilterRequest walletFilterRequest);
}
