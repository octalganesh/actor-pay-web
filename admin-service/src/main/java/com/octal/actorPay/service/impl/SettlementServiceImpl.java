package com.octal.actorPay.service.impl;

import com.octal.actorPay.client.AdminUserService;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SettlementServiceImpl implements SettlementService {

    @Autowired
    private AdminUserService adminUserService;

    @Override
    public void doSettlement(String userType) {


    }

    @Override
    public ResponseEntity<ApiResponse> findBySettlementStatus(String settlementStatus) {
        ResponseEntity<ApiResponse> apiResponse =
                adminUserService.findBySettlementStatus(settlementStatus);
        return apiResponse;
    }
}
