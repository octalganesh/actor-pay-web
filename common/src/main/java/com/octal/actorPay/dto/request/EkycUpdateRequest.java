package com.octal.actorPay.dto.request;

import com.octal.actorPay.constants.EkycStatus;
import lombok.Data;

import java.io.Serializable;

@Data
public class EkycUpdateRequest implements Serializable {

    private EkycStatus status;
    private String userId;
    private String docType;
    private String reason;

}
