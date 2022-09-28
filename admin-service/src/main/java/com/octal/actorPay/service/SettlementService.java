package com.octal.actorPay.service;

import com.octal.actorPay.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface SettlementService {

    void doSettlement(String userType);

    ResponseEntity<ApiResponse> findBySettlementStatus(String settlementStatus);

}
