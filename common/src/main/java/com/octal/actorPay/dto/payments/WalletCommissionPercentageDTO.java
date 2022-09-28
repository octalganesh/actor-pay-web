package com.octal.actorPay.dto.payments;


import com.octal.actorPay.dto.BaseDTO;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class WalletCommissionPercentageDTO extends BaseDTO {

    private String commissionPercentageId;

    private double percentageValue;

}
