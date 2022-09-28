package com.octal.actorPay.dto;

import lombok.Data;

import javax.persistence.Column;

@Data
public class MerchantOutletDTO extends BaseDTO{

    private String id;

    private String merchantId;

    private String outletId;

}
