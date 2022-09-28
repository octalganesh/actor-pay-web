package com.octal.actorPay.service;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.BankTransactionDTO;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.payments.BankTransferRequest;
import com.octal.actorPay.dto.payments.RazorpayPayoutRequest;
import com.octal.actorPay.dto.request.BankTransactionFilterRequest;

import java.util.List;

public interface UserBankService {
    ApiResponse transferToAnotherBank(RazorpayPayoutRequest payoutRequest, boolean isWallet) throws Exception;

    ApiResponse transferCheckOut(BankTransferRequest bankTransferRequest) throws Exception;

    PageItem<BankTransactionDTO> searchBankTransaction(Integer pageNo, Integer pageSize, String sortBy, boolean asc, String userType, BankTransactionFilterRequest filterRequest) throws Exception;

    List<BankTransactionDTO> getAllTransactionForReport(BankTransactionFilterRequest filterRequest);
}
