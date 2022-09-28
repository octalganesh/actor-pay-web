package com.octal.actorPay.model.entities;

import com.octal.actorPay.dto.payments.PayoutResponse;
import com.octal.actorPay.dto.payments.RefundResponse;
import com.octal.actorPay.entities.AbstractPersistable;
import com.octal.actorPay.entities.RefundResponseConverter;
import com.octal.actorPay.entities.ResponseConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "qr_code_transaction")
public class QrCodeTransactions extends AbstractPersistable {

    @Column(name = "amount",nullable = false)
    private double amount;

    @Column(name = "ifsc")
    private String ifsc;

    @Column(name ="account_holder_name")
    private String accountHolderName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "status")
    private String status;

    @Column(name = "reason")
    private String reason;

    @Column(name = "payout_id")
    private String payoutId;

    @Column(name = "qr_transaction_id")
    private String qrTransactionId;

    @Convert(converter = ResponseConverter.class)
    @Column(name = "payout_response")
    private PayoutResponse payoutResponse;

    @Column(name = "transaction_type")
    private String transactionType;

    @Convert(converter = RefundResponseConverter.class)
    @Column(name = "refund_response")
    private RefundResponse refundResponse;
}
