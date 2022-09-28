package com.octal.actorPay.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "order_note")
public class OrderNote extends AbstractPersistable{

    @Column(name = "order_note_by",nullable = false)
    private String orderNoteBy;

    @Column(name = "user_id",nullable = false)
    private String userId;

    @Column(name = "merchant_id",nullable = false)
    private String merchantId;

    @Column(name = "user_type",nullable = false)
    private String userType;

    @Column(name = "order_note_description",nullable = true)
    private String orderNoteDescription;

    @ManyToOne
    @JoinColumn(name = "order_id")
    OrderDetails orderDetails;

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getOrderNoteDescription() {
        return orderNoteDescription;
    }

    public void setOrderNoteDescription(String orderNoteDescription) {
        this.orderNoteDescription = orderNoteDescription;
    }

    public String getOrderNoteBy() {
        return orderNoteBy;
    }

    public void setOrderNoteBy(String orderNoteBy) {
        this.orderNoteBy = orderNoteBy;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
}
