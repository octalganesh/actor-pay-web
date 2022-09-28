package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


@Data
public class BankTransferRequest {
    private String name;

    private String email;

    private String contact;

    private String accountType;
    @NotEmpty
    private String accountHolderName;

    private String userId;

    @NotEmpty
    @Size(min = 11,max = 11, message = "The ifsc must be 11 characters")
    private String ifscCode;

    @NotEmpty
    private String accountNumber;

    private String userType;


    private double amount;

    private String narration;

    @JsonProperty("isWalletTransfer")
    private boolean isWalletTransfer;
}
