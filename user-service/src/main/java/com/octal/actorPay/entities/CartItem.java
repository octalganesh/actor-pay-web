package com.octal.actorPay.entities;

import com.octal.actorPay.dto.ApplyPromoCodeResponse;

import javax.persistence.*;

@Entity
@Table(name = "cart_item")
public class CartItem extends AbstractPersistable{

    @Column(name = "product_id",nullable = false)
    private String productId;

    @Column(name = "product_price",nullable = false)
    private double productPrice;

    @Column(name = "product_qty",nullable = false)
    private int productQty;

    @Column(name = "product_sgst",nullable = false)
    private double productSgst;

    @Column(name = "product_cgst",nullable = false)
    private double productCgst;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "merchant_id",nullable = false)
    private String merchantId;

    @Column(name = "total_price",nullable = false)
    private double totalPrice;

    @Column(name = "shipping_charge",nullable = false)
    private int shippingCharge;

    @Column(name = "tax_percentage",nullable = false)
    private double taxPercentage;

    @Column(name = "taxable_value",nullable = false)
    private double taxableValue;

    @Column(name = "image",nullable = true)
    private String image;

    @Column(name = "promo_code")
    private String promoCode;

    @Convert(converter = PromoResponseConverter.class)
    @Column(name = "promo_code_response")
    private ApplyPromoCodeResponse promoCodeResponse;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
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

    public ApplyPromoCodeResponse getPromoCodeResponse() {
        return promoCodeResponse;
    }

    public void setPromoCodeResponse(ApplyPromoCodeResponse promoCodeResponse) {
        this.promoCodeResponse = promoCodeResponse;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }
}
