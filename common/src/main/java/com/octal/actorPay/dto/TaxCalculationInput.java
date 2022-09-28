package com.octal.actorPay.dto;


public class TaxCalculationInput {

    private double unitPrice;
    private double taxPercentage;
    private int quantity;

    public TaxCalculationInput(double unitPrice,double taxPercentage,int quantity) {
        this.unitPrice=unitPrice;
        this.taxPercentage=taxPercentage;
        this.quantity=quantity;
    }
    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(double taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
