package com.octal.actorPay.dto.payments;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderRequest implements Serializable {

    private Double amount;
    private String currency;
    private String receipt;

}
