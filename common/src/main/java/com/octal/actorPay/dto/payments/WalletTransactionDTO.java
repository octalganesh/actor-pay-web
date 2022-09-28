package com.octal.actorPay.dto.payments;

import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.constants.TransactionTypes;
import com.octal.actorPay.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class WalletTransactionDTO extends BaseDTO {

    private String walletTransactionId;

    private Double transactionAmount;

    private TransactionTypes transactionTypes;

    private String userType;

    private String userId;

    private String userName;
    private String email;
    private String mobileNo;

    private String walletId;

    private String toUser;
    private String toUserName;
    private String toEmail;
    private String toMobileNo;

    private Double adminCommission;

    private Double transferAmount;

    private PurchaseType purchaseType;

    private String transactionRemark;

    private String parentTransaction;

    private Double percentage;

    private String transactionReason;
}