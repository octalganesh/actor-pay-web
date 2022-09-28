package com.octal.actorPay.dto;

import com.octal.actorPay.dto.payments.PayoutResponse;
import com.octal.actorPay.dto.payments.RefundResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BankTransactionDTO extends BaseDTO {

    private String bankTransactionId;

    private Double amount;

    private String userId;

    private String userName;

    private String email;

    private String mobileNo;

    private String ifsc;

    private String accountHolderName;

    private String accountNumber;

    private String transactionRemark;

    private String userType;

    private PayoutResponse payoutResponse;

    private RefundResponse refundResponse;

    private String transactionType;

    private String status;

    private String walletTransactionId;
}