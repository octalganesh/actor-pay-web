package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDTO {


    private int totalQuantity;

    private double totalPrice;

    private double totalSgst;

    private double totalCgst;

    private double TotalTaxableValue;

    private String userId;

    private String merchantId;

    private String merchantName;

    private ApplyPromoCodeResponse promoCodeResponse;

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public ApplyPromoCodeResponse getPromoCodeResponse() {
        return promoCodeResponse;
    }

    public void setPromoCodeResponse(ApplyPromoCodeResponse promoCodeResponse) {
        this.promoCodeResponse = promoCodeResponse;
    }

    private List<CartItemDTO> cartItemDTOList;

    public List<CartItemDTO> getCartItemDTOList() {
        return cartItemDTOList;
    }

    public void setCartItemDTOList(List<CartItemDTO> cartItemDTOList) {
        this.cartItemDTOList = cartItemDTOList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalSgst() {
        return totalSgst;
    }

    public void setTotalSgst(double totalSgst) {
        this.totalSgst = totalSgst;
    }

    public double getTotalCgst() {
        return totalCgst;
    }

    public void setTotalCgst(double totalCgst) {
        this.totalCgst = totalCgst;
    }

    public double getTotalTaxableValue() {
        return TotalTaxableValue;
    }

    public void setTotalTaxableValue(double totalTaxableValue) {
        TotalTaxableValue = totalTaxableValue;
    }
}
