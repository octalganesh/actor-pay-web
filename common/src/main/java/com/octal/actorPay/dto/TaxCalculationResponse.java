package com.octal.actorPay.dto;


public class TaxCalculationResponse {

    private double totalAmount; // unit price * quantity (with Tax)
    private double taxAmount; // Only Tax Amount
    private double grandTotal; // Total amount with Tax
    private double productPriceExcludingTax; // unit price * quantity without tax
    private double sGst;
    private double cGst;

    public double getProductPriceExcludingTax() {
        return productPriceExcludingTax;
    }

    public void setProductPriceExcludingTax(double productPriceExcludingTax) {
        this.productPriceExcludingTax = productPriceExcludingTax;
    }

    public double getsGst() {
        return sGst;
    }

    public void setsGst(double sGst) {
        this.sGst = sGst;
    }

    public double getcGst() {
        return cGst;
    }

    public void setcGst(double cGst) {
        this.cGst = cGst;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }
}
