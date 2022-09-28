package com.octal.actorPay.service;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.BankTransferRequest;
import com.octal.actorPay.dto.payments.RazorpayPayoutRequest;
import com.octal.actorPay.dto.request.BankTransactionFilterRequest;
import org.springframework.http.ResponseEntity;

public interface MerchantBankService {

    ResponseEntity<ApiResponse> transferToAnotherBank(RazorpayPayoutRequest payoutRequest) throws Exception;

    ResponseEntity<ApiResponse> transferCheckOut(BankTransferRequest bankTransferRequest) throws Exception;

    ResponseEntity<ApiResponse> searchBankTransaction(Integer pageNo, Integer pageSize, String sortBy, boolean asc, String userType, BankTransactionFilterRequest filterRequest) throws Exception;
}
