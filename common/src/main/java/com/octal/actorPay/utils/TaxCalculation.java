package com.octal.actorPay.utils;

import com.octal.actorPay.dto.TaxCalculationInput;
import com.octal.actorPay.dto.TaxCalculationResponse;
import org.springframework.stereotype.Component;

@Component
public class TaxCalculation {

//    private double unitPrice = 0d;
//    private float taxPercentage = 0f;
//    private double totalAmount = 0d;
//    private double price = 0d;
//    private double taxAmount = 0d;
//    private int quantity;

    // Calculate tox for per product with number of quantity
    public TaxCalculationResponse calculateTax(TaxCalculationInput calculationInput) {
        TaxCalculationResponse taxCalculationResponse = new TaxCalculationResponse();
        double unitPrice = calculationInput.getUnitPrice();
        double taxPercentage = calculationInput.getTaxPercentage();
        int quantity = calculationInput.getQuantity();
        double taxAmountSingleUnit = 0d;
        taxCalculationResponse.setTotalAmount(unitPrice * quantity);
        if (taxPercentage > 0) {
            taxAmountSingleUnit = (unitPrice * taxPercentage) / 100; // Tqx amount
            double taxAmountTotalUnit = quantity * taxAmountSingleUnit;
            taxCalculationResponse.setTaxAmount(taxAmountTotalUnit); // Total tax amount
            double productPriceExcludingTax = unitPrice  - taxAmountTotalUnit;
            taxCalculationResponse.setProductPriceExcludingTax(productPriceExcludingTax);
            double sGst = taxAmountTotalUnit/2;
            double cGst = taxAmountTotalUnit/2;
            taxCalculationResponse.setGrandTotal(taxCalculationResponse.getTotalAmount() + taxCalculationResponse.getTaxAmount());
            taxCalculationResponse.setsGst(sGst);
            taxCalculationResponse.setcGst(cGst);
        }
        return taxCalculationResponse;
    }
}
