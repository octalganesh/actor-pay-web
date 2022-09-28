package com.octal.actorPay.dto.payments;

import lombok.Data;

import java.io.Serializable;

@Data
public class RefundRequest implements Serializable {

    private Double refundAmount;
    private String paymentId;

}
