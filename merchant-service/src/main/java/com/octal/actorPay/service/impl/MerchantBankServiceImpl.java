package com.octal.actorPay.service.impl;

import com.octal.actorPay.client.UserServiceClient;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.BankTransferRequest;
import com.octal.actorPay.dto.payments.RazorpayPayoutRequest;
import com.octal.actorPay.dto.request.BankTransactionFilterRequest;
import com.octal.actorPay.service.MerchantBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MerchantBankServiceImpl implements MerchantBankService {
    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    public ResponseEntity<ApiResponse> transferToAnotherBank(RazorpayPayoutRequest request) throws Exception {
        return userServiceClient.transferToAnotherBank(request);
    }

    @Override
    public ResponseEntity<ApiResponse> transferCheckOut(BankTransferRequest bankTransferRequest) throws Exception {
        return userServiceClient.transferCheckOut(bankTransferRequest);
    }

    @Override
    public ResponseEntity<ApiResponse> searchBankTransaction(Integer pageNo, Integer pageSize, String sortBy,
                                                             boolean asc, String userType, BankTransactionFilterRequest filterRequest) throws Exception {

        return userServiceClient.searchBankTransaction(pageNo, pageSize, sortBy, asc, filterRequest.getUserType(), filterRequest);
    }

}
