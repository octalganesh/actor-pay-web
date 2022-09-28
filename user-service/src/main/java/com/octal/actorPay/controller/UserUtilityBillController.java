package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.feign.clients.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserUtilityBillController {

    @Autowired
    private AdminClient adminClient;

    @GetMapping("/v1/utility/bill/link")
    public ResponseEntity<ApiResponse> getUtilityBillLink() {
        return adminClient.getUtilityBillLink();
    }
}
