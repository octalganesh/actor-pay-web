package com.octal.actorPay.dto.request;

import lombok.Data;

@Data
public class DeleteRequest {

    private String userId;
    private String reason;
}
