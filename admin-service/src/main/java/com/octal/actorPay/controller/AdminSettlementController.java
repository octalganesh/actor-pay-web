package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminSettlementController {

    @Autowired
    private SettlementService settlementService;

    @GetMapping("/settlement/{status}")
    ResponseEntity<ApiResponse> findBySettlementStatus(@PathVariable(name = "status") String settlementStatus) {
        return settlementService.findBySettlementStatus(settlementStatus);
    }
}
