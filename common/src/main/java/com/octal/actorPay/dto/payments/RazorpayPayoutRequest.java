package com.octal.actorPay.dto.payments;

import lombok.Data;

@Data
public class RazorpayPayoutRequest {
    private String name;

    private String email;

    private String contact;

    private String accountType;

    private String userId;

    private String userType;

    private double amount;

    private PaymentGatewayResponse gatewayResponse;
}
