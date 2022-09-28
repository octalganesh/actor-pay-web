package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.octal.actorPay.entities.AbstractPersistable;
import com.octal.actorPay.entities.User;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemDTO extends BaseDTO {

    private String cartItemId;

    private String productName;

    private String productId;

    private double productPrice;

    private int productQty;

    private double productSgst;

    private double productCgst;

    private UserDTO userDTO;

    private String merchantId;

    private String merchantName;

    private double totalPrice;

    private int shippingCharge;

    private double taxPercentage;

    private double taxableValue;

    private String email;

    private String image;

    private String promoCode;

    private ApplyPromoCodeResponse promoCodeResponse;

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }

    public double getTaxableValue() {
        return taxableValue;
    }

    public void setTaxableValue(double taxableValue) {
        this.taxableValue = taxableValue;
    }

    public double getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(double taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
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

    public ApplyPromoCodeResponse getPromoCodeResponse() {
        return promoCodeResponse;
    }

    public void setPromoCodeResponse(ApplyPromoCodeResponse promoCodeResponse) {
        this.promoCodeResponse = promoCodeResponse;
    }
}
