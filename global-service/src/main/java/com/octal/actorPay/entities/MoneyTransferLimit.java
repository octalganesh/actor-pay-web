package com.octal.actorPay.entities;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "money_transfer_limit")
@DynamicUpdate
public class MoneyTransferLimit extends AbstractPersistable{

    @Column(name = "customer_add_money")
    private BigDecimal customerAddMoney;

    @Column(name = "customer_withdraw_to_bank")
    private BigDecimal customerWithdrawMoneyToBank;

    @Column(name = "customer_transaction_limit")
    private BigDecimal customerTransactionLimit;

    @Column(name = "merchant_add_money")
    private BigDecimal merchantAddMoney;

    @Column(name = "merchant_withdraw_to_bank")
    private BigDecimal merchantWithdrawMoneyToBank;

    @Column(name = "merchant_transaction_limit")
    private BigDecimal merchantTransactionLimit;

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
}
