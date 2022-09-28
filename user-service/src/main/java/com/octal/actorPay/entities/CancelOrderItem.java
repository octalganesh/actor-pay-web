package com.octal.actorPay.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "cancel_order_item")
public class CancelOrderItem extends AbstractPersistable{


    @Column(name = "order_item_id")
    private String orderItemId;

    @Column(name = "refund_amount",nullable = false)
    private double refundAmount;

    @Column(name = "charge_amount")
    private double chargeAmount;

    @Column(name = "original_amount")
    private double originalAmount;

    @Column(name = "cancel_reason")
    private String cancelReason;

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cancel_order_id")
    private CancelOrder cancelOrder;

    public double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public double getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public CancelOrder getCancelOrder() {
        return cancelOrder;
    }

    public void setCancelOrder(CancelOrder cancelOrder) {
        this.cancelOrder = cancelOrder;
    }
}
