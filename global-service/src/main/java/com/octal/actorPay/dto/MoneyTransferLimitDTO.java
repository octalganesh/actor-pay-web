package com.octal.actorPay.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MoneyTransferLimitDTO {

    private String id;
    private BigDecimal customerAddMoney;
    private BigDecimal customerWithdrawMoneyToBank;
    private BigDecimal customerTransactionLimit;
    private BigDecimal merchantAddMoney;
    private BigDecimal merchantWithdrawMoneyToBank;
    private BigDecimal merchantTransactionLimit;
    private LocalDateTime updatedAt;

    public MoneyTransferLimitDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getCustomerAddMoney() {
        return customerAddMoney;
    }

    public void setCustomerAddMoney(BigDecimal customerAddMoney) {
        this.customerAddMoney = customerAddMoney;
    }

    public BigDecimal getCustomerWithdrawMoneyToBank() {
        return customerWithdrawMoneyToBank;
    }

    public void setCustomerWithdrawMoneyToBank(BigDecimal customerWithdrawMoneyToBank) {
        this.customerWithdrawMoneyToBank = customerWithdrawMoneyToBank;
    }

    public BigDecimal getCustomerTransactionLimit() {
        return customerTransactionLimit;
    }

    public void setCustomerTransactionLimit(BigDecimal customerTransactionLimit) {
        this.customerTransactionLimit = customerTransactionLimit;
    }

    public BigDecimal getMerchantAddMoney() {
        return merchantAddMoney;
    }

    public void setMerchantAddMoney(BigDecimal merchantAddMoney) {
        this.merchantAddMoney = merchantAddMoney;
    }

    public BigDecimal getMerchantWithdrawMoneyToBank() {
        return merchantWithdrawMoneyToBank;
    }

    public void setMerchantWithdrawMoneyToBank(BigDecimal merchantWithdrawMoneyToBank) {
        this.merchantWithdrawMoneyToBank = merchantWithdrawMoneyToBank;
    }

    public BigDecimal getMerchantTransactionLimit() {
        return merchantTransactionLimit;
    }

    public void setMerchantTransactionLimit(BigDecimal merchantTransactionLimit) {
        this.merchantTransactionLimit = merchantTransactionLimit;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
