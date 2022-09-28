package com.octal.actorPay.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.LocalDate;

public class ProductCommissionFilterRequest {

    private String merchantId;

    private String merchantName;

    private Double merchantEarnings;

    private Double merchantEarningsRangeFrom;

    private Double merchantEarningsRangeTo;

    private Double actorCommissionAmtRangeFrom;

    private Double actorCommissionAmtRangeTo;

    private Double actorCommissionAmt;

    private String productName;

    private String orderStatus;

    private String settlementStatus;

    private Boolean status;

    private String orderNo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

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

    public Double getActorCommissionAmt() {
        return actorCommissionAmt;
    }

    public void setActorCommissionAmt(Double actorCommissionAmt) {
        this.actorCommissionAmt = actorCommissionAmt;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public Double getMerchantEarningsRangeFrom() {
        return merchantEarningsRangeFrom;
    }

    public void setMerchantEarningsRangeFrom(Double merchantEarningsRangeFrom) {
        this.merchantEarningsRangeFrom = merchantEarningsRangeFrom;
    }

    public Double getMerchantEarningsRangeTo() {
        return merchantEarningsRangeTo;
    }

    public void setMerchantEarningsRangeTo(Double merchantEarningsRangeTo) {
        this.merchantEarningsRangeTo = merchantEarningsRangeTo;
    }

    public Double getActorCommissionAmtRangeFrom() {
        return actorCommissionAmtRangeFrom;
    }

    public void setActorCommissionAmtRangeFrom(Double actorCommissionAmtRangeFrom) {
        this.actorCommissionAmtRangeFrom = actorCommissionAmtRangeFrom;
    }

    public Double getActorCommissionAmtRangeTo() {
        return actorCommissionAmtRangeTo;
    }

    public void setActorCommissionAmtRangeTo(Double actorCommissionAmtRangeTo) {
        this.actorCommissionAmtRangeTo = actorCommissionAmtRangeTo;
    }
}
