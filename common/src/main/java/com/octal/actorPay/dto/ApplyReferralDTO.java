package com.octal.actorPay.dto;

import lombok.Data;

@Data
public class ApplyReferralDTO {

    private String orderNo;
    private String userReferredId;
    private double transferredAmount;
}
