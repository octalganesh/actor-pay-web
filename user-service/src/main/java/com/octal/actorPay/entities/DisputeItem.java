package com.octal.actorPay.entities;

import javax.persistence.*;

@Entity
@Table(name = "dispute_item")
public class DisputeItem extends AbstractPersistable {

    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "description",nullable = false)
    private String description;

    @Column(name = "image_path")
    private String imagePath;

    @OneToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem ;

    @Column(name = "penality_percentage",nullable = true)
    private Double penalityPercentage;

    @Column(name = "status")
    private String status;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "dispute_flag")
    private boolean disputeFlag;

    @Column(name = "dispute_code",unique = true)
    private String disputeCode;

    @Column(name = "order_no")
    private String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDisputeCode() {
        return disputeCode;
    }

    public void setDisputeCode(String disputeCode) {
        this.disputeCode = disputeCode;
    }

    public boolean isDisputeFlag() {
        return disputeFlag;
    }

    public void setDisputeFlag(boolean disputeFlag) {
        this.disputeFlag = disputeFlag;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public Double getPenalityPercentage() {
        return penalityPercentage;
    }

    public void setPenalityPercentage(Double penalityPercentage) {
        this.penalityPercentage = penalityPercentage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
