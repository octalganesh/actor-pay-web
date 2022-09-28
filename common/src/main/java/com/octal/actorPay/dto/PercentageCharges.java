package com.octal.actorPay.dto;

public class PercentageCharges {

    private double percentageCharges;

    private double balanceAmount;

    private double originalAmount;

    private double percentage;

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public double getPercentageCharges() {
        return percentageCharges;
    }

    public void setPercentageCharges(double percentageCharges) {
        this.percentageCharges = percentageCharges;
    }

    public double getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(double originalAmount) {
        this.originalAmount = originalAmount;
    }
}
