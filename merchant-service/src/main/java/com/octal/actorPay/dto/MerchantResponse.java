package com.octal.actorPay.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MerchantResponse {

    private String userId;
    private String merchantId;

}
