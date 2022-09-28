package com.octal.actorPay.dto.request;

import lombok.Data;

@Data
public class PromoCodeFilter {
    private String userId;
    private String promoCode;
    private Float amount;
}
