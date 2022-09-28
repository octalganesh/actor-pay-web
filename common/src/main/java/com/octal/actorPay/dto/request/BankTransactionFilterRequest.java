package com.octal.actorPay.dto.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class BankTransactionFilterRequest {
    private String userId;

    private String adminId;

    private String name;

    private String email;

    private String contact;

    private String userType;

    private Double transferAmountFrom;

    private Double transferAmountTo;

    private String transactionRemark;

    private String bankTransactionId;

    private String accountNumber;

    private String accountHolderNumber;

    private String ifscCode;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private String transactionType;

    private String status;

}
