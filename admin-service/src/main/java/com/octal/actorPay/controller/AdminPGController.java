package com.octal.actorPay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.client.AdminPaymentClient;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.ContactRequest;
import com.octal.actorPay.dto.payments.RefundRequest;
import com.octal.actorPay.dto.payments.RefundResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/pg")
public class AdminPGController {

    @Autowired
    private AdminPaymentClient adminPaymentClient;

    @Secured("ROLE_FUND_ACCOUNT_CREATE")
    @PostMapping("/refund")
    public ResponseEntity<ApiResponse> doRefund(@RequestBody RefundRequest refundRequest) {
        ApiResponse apiResponse = adminPaymentClient.doRefund(refundRequest);
        ObjectMapper mapper = new ObjectMapper();
        RefundResponse refundResponse = mapper.convertValue(apiResponse.getData(), RefundResponse.class);
        System.out.println("Refund Response " + refundResponse.getStatus());
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }

    @Secured("ROLE_FUND_CONTACT_CREATE")
    @PostMapping("/contacts/create")
    public ResponseEntity<ApiResponse> createContact(@RequestBody ContactRequest contactRequest) throws Exception {
        ApiResponse apiResponse = adminPaymentClient.createContact(contactRequest);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }
}