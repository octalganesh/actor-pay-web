package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PayoutRequest implements Serializable {

    @JsonProperty("account_number")
    private String accountNumber;
    @JsonProperty("fund_account_id")
    private String fundAccountId;
    private Integer amount;
    private String currency;
    private String mode;
    private String purpose;
    @JsonProperty("reference_id")
    private String referenceId;
    private String narration;
    @JsonProperty("queue_if_low_balance")
    private Boolean queueIfLowBalance;
}
