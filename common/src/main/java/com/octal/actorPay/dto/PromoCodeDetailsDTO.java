package com.octal.actorPay.dto;

import lombok.Data;

@Data
public class PromoCodeDetailsDTO extends BaseDTO {

    private String userId;

    private String promoCode;

    private String usedCount;

    private float discountAmount;

    private String orderId;
    private String orderItemId;
}
