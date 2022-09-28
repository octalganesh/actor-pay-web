package com.octal.actorPay.dto.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminWalletTransferRequest implements Serializable {

    private Double transferAmount;
    private String userFrom;
    private String walletTransactionId;
}
