package com.octal.actorPay.controller;

import com.octal.actorPay.client.AdminFeignClient;
import com.octal.actorPay.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MerchantUtilityBillController {

    @Autowired
    private AdminFeignClient adminFeignClient;

    @GetMapping("/v1/utility/bill/link")
    public ResponseEntity<ApiResponse> getUtilityBillLink() {
        return adminFeignClient.getUtilityBillLink();
    }
}
