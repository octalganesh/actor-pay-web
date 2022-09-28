package com.octal.actorPay.dto.payments;

import lombok.Data;

@Data
public class DeactivateRequest extends DeactivatePgRequest {

    private String userId;
    private String userType;
    private String fundAccountId;
}
