package com.octal.actorPay.dto;

import lombok.Data;

@Data
public class PgDetailsDTO extends BaseDTO {

    private String paymentId;

    private String pgOrderId;

    private String pgSignature;

    private String paymentMethod;

    private String paymentTypeId;
}
