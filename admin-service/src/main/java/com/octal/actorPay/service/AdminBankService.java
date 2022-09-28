package com.octal.actorPay.service;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.request.BankTransactionFilterRequest;
import org.springframework.http.ResponseEntity;

public interface AdminBankService {
    ResponseEntity<ApiResponse> searchBankTransaction(Integer pageNo, Integer pageSize, String sortBy, boolean asc, BankTransactionFilterRequest filterRequest);
}
