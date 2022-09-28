package com.octal.actorPay.dto.request;

import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.constants.TransactionTypes;
import com.octal.actorPay.entities.User;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class WalletFilterRequest implements Serializable {

    private String userId;

//    private User user;

    private String userEmail;

    private String receiverEmail;

    private String userType;

    private Double adminCommissionFrom;

    private Double adminCommissionTo;

    private Double transactionAmountFrom;

    private Double transactionAmountTo;

    private Double transferAmountFrom;

    private Double transferAmountTo;

    private String transactionRemark;

    private String toUser;

    private Double percentage;

    private TransactionTypes transactionTypes;

    private PurchaseType purchaseType;

    private String walletTransactionId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private String adminUserId;

    private String parentTransaction;


}
