package com.octal.actorPay.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CovertRewardsRequest {

    private String userId;
    private Long rewardPoint;
    private Double transferredAmount;
}
