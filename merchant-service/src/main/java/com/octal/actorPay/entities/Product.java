package com.octal.actorPay.entities;

import javax.persistence.*;
import javax.validation.constraints.Max;

@Entity
@Table(name = "product")
public class Product extends AbstractPersistable{

    public Product() {
    }

    @Column(name = "name",length = 255,unique = true)
    private String name;

    @Column(name = "description",nullable = true,length = 500)
    private String description;

    @JoinColumn(name = "category_id")
    private String categoryId;

    @JoinColumn(name = "sub_category_id")
    private String subcategoryId;

    @Column(name = "actual_price")
    private Double actualPrice;

    @Column(name = "deal_price")
    private Double dealPrice;

    @Column(name = "image",nullable = true)
    private String image;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "merchant_id")
    private MerchantDetails merchantDetails;

    @Column(name = "stock_count")
    private Integer stockCount;

    @Column(name = "tax_id")
    private String taxId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_tax_id")
    private ProductTax productTax;

    @Column(name = "status",nullable = false)
    private String stockStatus;

    @Column(name = "outlet_id")
    private String outletId;

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public Double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(Double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public Double getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(Double dealPrice) {
        this.dealPrice = dealPrice;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public MerchantDetails getMerchantDetails() {
        return merchantDetails;
    }

    public void setMerchantDetails(MerchantDetails merchantDetails) {
        this.merchantDetails = merchantDetails;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public ProductTax getProductTax() {
        return productTax;
    }

    public void setProductTax(ProductTax productTax) {
        this.productTax = productTax;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }
}
