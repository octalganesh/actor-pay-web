package com.octal.actorPay.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Author: Nancy Chauhan
 * This class represents the Loyalty Rewards points related data.
 */
public class LRPointsDTO {
    private String id;
    private BigDecimal addMoney;
    private BigDecimal payTransferToWallet;
    private BigDecimal payNFC;
    private BigDecimal buyDeals;
    private BigDecimal thresholdValueToRedeemPoints;
    private BigDecimal conversionRateOfLoyaltyPoints;
    private BigDecimal conversionRateOfLoyaltyCurrency;
    private LocalDateTime updatedAt;

    public LRPointsDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getAddMoney() {
        return addMoney;
    }

    public void setAddMoney(BigDecimal addMoney) {
        this.addMoney = addMoney;
    }

    public BigDecimal getPayTransferToWallet() {
        return payTransferToWallet;
    }

    public void setPayTransferToWallet(BigDecimal payTransferToWallet) {
        this.payTransferToWallet = payTransferToWallet;
    }

    public BigDecimal getPayNFC() {
        return payNFC;
    }

    public void setPayNFC(BigDecimal payNFC) {
        this.payNFC = payNFC;
    }

    public BigDecimal getBuyDeals() {
        return buyDeals;
    }

    public void setBuyDeals(BigDecimal buyDeals) {
        this.buyDeals = buyDeals;
    }

    public BigDecimal getThresholdValueToRedeemPoints() {
        return thresholdValueToRedeemPoints;
    }

    public void setThresholdValueToRedeemPoints(BigDecimal thresholdValueToRedeemPoints) {
        this.thresholdValueToRedeemPoints = thresholdValueToRedeemPoints;
    }

    public BigDecimal getConversionRateOfLoyaltyPoints() {
        return conversionRateOfLoyaltyPoints;
    }

    public void setConversionRateOfLoyaltyPoints(BigDecimal conversionRateOfLoyaltyPoints) {
        this.conversionRateOfLoyaltyPoints = conversionRateOfLoyaltyPoints;
    }

    public BigDecimal getConversionRateOfLoyaltyCurrency() {
        return conversionRateOfLoyaltyCurrency;
    }

    public void setConversionRateOfLoyaltyCurrency(BigDecimal conversionRateOfLoyaltyCurrency) {
        this.conversionRateOfLoyaltyCurrency = conversionRateOfLoyaltyCurrency;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}