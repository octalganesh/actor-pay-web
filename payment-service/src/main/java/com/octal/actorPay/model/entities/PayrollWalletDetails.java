package com.octal.actorPay.model.entities;

import com.octal.actorPay.entities.AbstractPersistable;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "payroll_wallet_details")
@Data
public class PayrollWalletDetails extends AbstractPersistable {

    @Column(name  = "receiver_user_id")
    private String receiverUserId;

    @Column(name  = "receiver_email")
    private String receiverEmail;

    @Column(name  = "merchantId")
    private String merchantId;

    @Column(name  = "status")
    private String status;

    @Column(name  = "amount")
    private String amount;

    @Column(name = "transactionId")
    private String transactionId;

    @Column(name = "receiverName")
    private String receiverName;
}
