package com.octal.actorPay.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "referred_history")
@Data
public class ReferralHistory extends AbstractPersistable {

    @Column(name = "user_id")
    String userId;

    @Column(name = "referred_user_id")
    String referredUserId;

    @Column(name = "amount")
    double amount;

    @Column(name = "referred_user_name")
    String referredUserName;

    @Column(name = "wallet_transaction_id")
    String walletTransactionId;

    @Column(name = "order_no")
    String orderNo;

}
