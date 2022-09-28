package com.octal.actorPay.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DeactivateLoyaltyRewardRequest {

    @JsonProperty("active")
    private boolean active;

    private String rewardId;
}
