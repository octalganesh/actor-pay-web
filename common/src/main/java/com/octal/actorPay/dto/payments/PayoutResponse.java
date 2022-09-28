package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PayoutResponse implements Serializable {

    @JsonProperty("id")
    private String payoutId;

    @JsonProperty("amount")
    private Integer amount;
    private String accountNumber;
    private String currency;
    private String mode;
    private String purpose;
    private String fees;
    private String tax;
    private String status;
    private String narration;
    @JsonProperty("reference_id")
    private String referenceId;
    @JsonProperty("isWalletTransfer")
    private boolean isWalletTransfer;

}
