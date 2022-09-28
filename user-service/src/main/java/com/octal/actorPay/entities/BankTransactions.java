package com.octal.actorPay.entities;

import com.octal.actorPay.dto.payments.PayoutResponse;
import com.octal.actorPay.dto.payments.RefundResponse;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "bank_transactions")
@Data
public class BankTransactions  extends AbstractPersistable{

    @Column(name = "amount",nullable = false)
    private double amount;

    @Column(name = "ifsc")
    private String ifsc;

    @Column(name ="account_holder_name")
    private String accountHolderName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "order_receipt")
    private String orderReceipt;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_type")
    private String userType;

    @Column(name = "status")
    private String status;

    @Column(name = "reason")
    private String reason;

    @Column(name = "payout_id")
    private String payoutId;

    @Column(name = "bank_transaction_id")
    private String bankTransactionId;

    @Convert(converter = ResponseConverter.class)
    @Column(name = "payout_response")
    private PayoutResponse payoutResponse;

    @Column(name = "transaction_type")
    private String transactionType;

    @Convert(converter = RefundResponseConverter.class)
    @Column(name = "refund_response")
    private RefundResponse refundResponse;

    @Column(name = "refund_wallet_trans_id")
    private String refundWalletTransId;

}
