package com.octal.actorPay.dto.payments;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankAccountResponse implements Serializable {

    private String contactId;
    private String fundAccountId;
    private String ifscCode;
    private String name;
    private String accountNumber;
    private String bankName;
    private String accountType;
    private boolean active;
    private LocalDateTime createdAt;
    @JsonProperty("self")
    private boolean self;
    @JsonProperty("primaryAccount")
    private boolean primaryAccount;
    private QRCodeResponse qrCodeResponse;
}
