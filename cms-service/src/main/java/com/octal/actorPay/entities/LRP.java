package com.octal.actorPay.entities;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "lrp")
@DynamicUpdate
public class LRP extends AbstractPersistable {

    @Column(name = "add_money")
    private BigDecimal addMoney;

    @Column(name = "pay_transfer_to_wallet")
    private BigDecimal payTransferToWallet;

    @Column(name = "pay_nfc")
    private BigDecimal payNFC;

    @Column(name = "buy_deals")
    private BigDecimal buyDeals;

    @Column(name = "threshold_value")
    private BigDecimal thresholdValueToRedeemPoints;

    @Column(name = "conversion_rate_points")
    private BigDecimal conversionRateOfLoyaltyPoints;

    @Column(name = "conversion_rate_currency")
    private BigDecimal conversionRateOfLoyaltyCurrency;

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
}