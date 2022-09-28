package com.octal.actorPay.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "cancel_order")
public class CancelOrder extends AbstractPersistable{

    @Column(name = "refund_amount",nullable = false)
    private double refundAmount;

    @Column(name = "amount_charged",nullable = false)
    private double charges;

    @Column(name ="original_purchase_amount")
    private double originalAmount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id",unique = true)
    private OrderDetails orderDetails;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "image")
    private String image;

    @JsonBackReference
    @OneToMany(mappedBy = "cancelOrder", cascade = CascadeType.ALL)
    private List<CancelOrderItem> cancelOrderItems;

    public double getCharges() {
        return charges;
    }

    public void setCharges(double charges) {
        this.charges = charges;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public List<CancelOrderItem> getCancelOrderItems() {
        return cancelOrderItems;
    }

    public void setCancelOrderItems(List<CancelOrderItem> cancelOrderItems) {
        this.cancelOrderItems = cancelOrderItems;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }
}
