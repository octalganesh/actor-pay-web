package com.octal.actorPay.dto.payments;

import lombok.Data;

import java.io.Serializable;

@Data
public class PaymentGatewayResponse implements Serializable {

    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
    private String paymentMethod;

}
