package com.octal.actorPay.dto.payments;

import lombok.Data;

import java.io.Serializable;

@Data
public class PgSignatureRequest implements Serializable {

    private String paymentId;
    private String orderId;
    private String signature;

}


