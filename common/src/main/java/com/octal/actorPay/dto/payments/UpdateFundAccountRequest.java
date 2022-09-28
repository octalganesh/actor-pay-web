package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateFundAccountRequest {
    private String userId;
    private String userType;
    private String fundAccountId;
    @JsonProperty("self")
    private boolean self;
    @JsonProperty("primaryAccount")
    private boolean primaryAccount;
}
