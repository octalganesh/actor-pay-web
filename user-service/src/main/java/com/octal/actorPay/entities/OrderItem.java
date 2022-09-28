package com.octal.actorPay.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "order_item")
public class OrderItem extends AbstractPersistable{

    @Column(name = "product_id",nullable = false)
    private String productId;

    @Column(name = "product_price",nullable = false)
    private double productPrice;

    @Column(name = "actual_price",nullable = false)
    private double actualPrice;

    @Column(name = "product_qty",nullable = false)
    private int productQty;

    @Column(name = "product_sgst",nullable = false)
    private double productSgst;

    @Column(name = "product_cgst",nullable = false)
    private double productCgst;

    @Column(name = "total_price",nullable = false)
    private double totalPrice;

    @Column(name = "shipping_charge",nullable = false)
    private int shippingCharge;

    @Column(name = "tax_percentage",nullable = false)
    private double taxPercentage;

    @Column(name = "taxable_value",nullable = false)
    private double taxableValue;

    @Column(name = "category_id",nullable = false)
    private String categoryId;

    @Column(name = "subcategory_id",nullable = false)
    private String subcategoryId;

    @Column(name = "image",nullable = true)
    private String image;

    @Column(name = "merchant_id",nullable = false)
    private String merchantId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private OrderDetails orderDetails;

    @Column(name = "order_item_status",nullable = false)
    private String orderItemStatus;

    @OneToOne(mappedBy = "orderItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private OrderItemCommission orderItemCommission;

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public OrderItemCommission getOrderItemCommission() {
        return orderItemCommission;
    }

    public void setOrderItemCommission(OrderItemCommission orderItemCommission) {
        this.orderItemCommission = orderItemCommission;
    }

    public String getOrderItemStatus() {
        return orderItemStatus;
    }

    public void setOrderItemStatus(String orderItemStatus) {
        this.orderItemStatus = orderItemStatus;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQty() {
        return productQty;
    }

    public void setProductQty(int productQty) {
        this.productQty = productQty;
    }

    public double getProductSgst() {
        return productSgst;
    }

    public void setProductSgst(double productSgst) {
        this.productSgst = productSgst;
    }

    public double getProductCgst() {
        return productCgst;
    }

    public void setProductCgst(double productCgst) {
        this.productCgst = productCgst;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getShippingCharge() {
        return shippingCharge;
    }

    public void setShippingCharge(int shippingCharge) {
        this.shippingCharge = shippingCharge;
    }

    public double getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(double taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public double getTaxableValue() {
        return taxableValue;
    }

    public void setTaxableValue(double taxableValue) {
        this.taxableValue = taxableValue;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }
}
