package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.octal.actorPay.entities.AbstractPersistable;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CancelOrderDTO {

    private String userType;

    private double refundAmount;

    @NotEmpty
    private String cancellationRequest;

    private String cancelReason;

    private String image;

    private List<String> orderItemIds;

    private List<CancelOrderItemDTO> cancelOrderItemDTOs;

    public List<CancelOrderItemDTO> getCancelOrderItemDTOs() {
        return cancelOrderItemDTOs;
    }

    public void setCancelOrderItemDTOs(List<CancelOrderItemDTO> cancelOrderItemDTOs) {
        this.cancelOrderItemDTOs = cancelOrderItemDTOs;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getCancellationRequest() {
        return cancellationRequest;
    }

    public void setCancellationRequest(String cancellationRequest) {
        this.cancellationRequest = cancellationRequest;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public List<String> getOrderItemIds() {
        return orderItemIds;
    }

    public void setOrderItemIds(List<String> orderItemIds) {
        this.orderItemIds = orderItemIds;
    }
}
