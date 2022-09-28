package com.octal.actorPay.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tax")
public class Tax extends AbstractPersistable {

    @Column(name = "hsn_code",nullable = false,unique = true)
    private String hsnCode;

    @Column(name = "product_details",nullable = true)
    private String productDetails;

    @Column(name = "chapter",nullable = true)
    private String chapter;

    @Column(name = "tax_percentage",nullable = false)
    private Float taxPercentage;

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(String productDetails) {
        this.productDetails = productDetails;
    }

    public Float getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(Float taxPercentage) {
        this.taxPercentage = taxPercentage;
    }
}
