package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.octal.actorPay.entities.User;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplyPromoCodeResponse {

    private User user;
    private String userId;
    private Float amount;
    private Float discount;
    private Float amountAfterDiscount;
    private String promoCode;
    private String orderId;
    private String orderItemId;
}
