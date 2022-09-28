package com.octal.actorPay.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "product_commission")
public class ProductCommission extends AbstractPersistable {

    @Column(name = "actor_commission_amt", nullable = false)
    private Double actorCommissionAmt;

    @Column(name = "commission_percentage", nullable = false)
    private Double commissionPercentage;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_item_id",unique = true)
    private OrderItem orderItem;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "merchant_earnings")
    private Double merchantEarnings;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "settlement_status")
    private String settlementStatus;

    @Column(name = "order_status")
    private String orderStatus;

    public Double getActorCommissionAmt() {
        return actorCommissionAmt;
    }

    public void setActorCommissionAmt(Double actorCommissionAmt) {
        this.actorCommissionAmt = actorCommissionAmt;
    }

    public Double getCommissionPercentage() {
        return commissionPercentage;
    }

    public void setCommissionPercentage(Double commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public Double getMerchantEarnings() {
        return merchantEarnings;
    }

    public void setMerchantEarnings(Double merchantEarnings) {
        this.merchantEarnings = merchantEarnings;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
