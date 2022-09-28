package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletTransactionResponse implements Serializable {

    private Double walletBalance;

    private Double transferredAmount;

    @JsonProperty("transactionId")
    private String parentTransactionId;

    private String customerName;

    private String TransactionType;
    private String modeOfTransaction;
    private String fromCustomerEmail;
    private String toCustomerEmail;

    @JsonProperty("isWalletTransfer")
    private boolean isWalletTransfer;


}
