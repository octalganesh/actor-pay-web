package com.octal.actorPay.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class DisputeFilterRequest {


    private String title;
    private Double penalityPercentage;
    private String status;
    private String userId;
    private String merchantId;
    private Boolean disputeFlag;
    private String disputeCode;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    private String userType;

    private String orderNo;

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Boolean getDisputeFlag() {
        return disputeFlag;
    }

    public void setDisputeFlag(Boolean disputeFlag) {
        this.disputeFlag = disputeFlag;
    }

    public String getDisputeCode() {
        return disputeCode;
    }

    public void setDisputeCode(String disputeCode) {
        this.disputeCode = disputeCode;
    }
}
