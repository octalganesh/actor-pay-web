package com.octal.actorPay.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.octal.actorPay.entities.AbstractPersistable;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderNoteDTO extends BaseDTO {

    private String orderNoteId;

    private String orderId;

    private String orderNo;

    private String orderNoteBy;

    private String userId;

    private String merchantId;

    private String userType;

    private String orderNoteDescription;

    private List<String> orderItemIds;

    @JsonIgnore
    private String cancellationReason;

    @JsonIgnore
    private String image;

    @JsonIgnore
    private String noteFlowType;

    private String productName;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getNoteFlowType() {
        return noteFlowType;
    }

    public void setNoteFlowType(String noteFlowType) {
        this.noteFlowType = noteFlowType;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getOrderItemIds() {
        return orderItemIds;
    }

    public void setOrderItemIds(List<String> orderItemIds) {
        this.orderItemIds = orderItemIds;
    }

    public String getOrderNoteId() {
        return orderNoteId;
    }

    public void setOrderNoteId(String orderNoteId) {
        this.orderNoteId = orderNoteId;
    }

    public String getOrderNoteDescription() {
        return orderNoteDescription;
    }

    public void setOrderNoteDescription(String orderNoteDescription) {
        this.orderNoteDescription = orderNoteDescription;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

}
