package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderResponse implements Serializable {

    @JsonProperty("orderId")
    private String id;
    private String amount;
    private String currency;
    private String receipt;
    private String status;
}
