package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class RefundResponse implements Serializable {

    private String referenceId;

    private Integer amount;

    private String currency;

    private String status;

    private String paymentId;

    @JsonProperty("isWalletTransfer")
    private boolean isWalletTransfer;


}
