package com.octal.actorPay.service.impl;

import com.octal.actorPay.client.AdminUserService;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.request.BankTransactionFilterRequest;
import com.octal.actorPay.service.AdminBankService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class AdminBankServiceImpl implements AdminBankService {

    private AdminUserService adminUserService;

    public AdminBankServiceImpl(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @Override
    public ResponseEntity<ApiResponse> searchBankTransaction(Integer pageNo, Integer pageSize, String sortBy, boolean asc, BankTransactionFilterRequest filterRequest) {
        return adminUserService.searchBankTransaction(pageNo, pageSize, sortBy, asc, filterRequest.getUserType(), filterRequest);
    }
}
