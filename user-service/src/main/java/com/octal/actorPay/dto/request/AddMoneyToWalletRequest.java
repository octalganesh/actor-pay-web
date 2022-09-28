package com.octal.actorPay.dto.request;

import javax.validation.constraints.NotBlank;

public class AddMoneyToWalletRequest {

    @NotBlank
    private String userId;

    private String walletCode;

    private Double amount;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWalletCode() {
        return walletCode;
    }

    public void setWalletCode(String walletCode) {
        this.walletCode = walletCode;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
