package com.octal.actorPay.dto;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class PgUserAccountDTO implements Serializable {

    private String pgUserId;

    private String pgContactId;

    private String pgFundId;

    private String userId;

    private String userType;

    private String accountHolderName;

    private String ifscCode;

    private String accountNumber;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
