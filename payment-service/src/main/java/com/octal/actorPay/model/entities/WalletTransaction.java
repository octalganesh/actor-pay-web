package com.octal.actorPay.model.entities;

import com.octal.actorPay.constants.PurchaseType;
import com.octal.actorPay.entities.AbstractPersistable;
import com.octal.actorPay.constants.TransactionTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "wallet_transaction")
public class WalletTransaction extends AbstractPersistable {

    @Column(name = "wallet_transaction_id",nullable = false)
    private String walletTransactionId;

    @Column(name = "transaction_amount",nullable = false)
    private Double transactionAmount;

    @Enumerated(EnumType.STRING)
    private TransactionTypes transactionTypes;

    @Column(name = "user_type",nullable = false)
    private String userType;

    @Column(name = "user_id",nullable = false)
    private String userId;

    @OneToOne
    @JoinColumn(name = "wallet_id",nullable = false)
    private Wallet wallet;

    @Column(name = "to_user",nullable = true)
    private String toUser;

    @Column(name = "admin_commission",nullable = false)
    private Double adminCommission;

    @Column(name = "transfer_amount" ,nullable = false)
    private Double transferAmount;

    @Enumerated(EnumType.STRING)
    private PurchaseType purchaseType;

    @Column(name = "transaction_remark",nullable = false)
    private String transactionRemark;;

    @Column(name = "transaction_reason",nullable = false)
    private String transactionReason;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "parent_transaction")
    private String parentTransaction;

}