package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.octal.actorPay.dto.payments.PaymentGatewayResponse;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShippingAddressDTO extends AddressDTO {

    private String id;

    private String name;

    @NotBlank
    @Size(max = 3)
    private String extensionNumber;

    @NotBlank
    @Size(min = 5, max = 15)
    private String primaryContactNumber;

    private String secondaryContactNumber;

    private Boolean primary;

    private String userId;

    //@NotBlank
    @Size(max = 255)
    @JsonProperty("title")
    private String addressTitle;

    @Size(max = 255)
    @NotBlank
    private String area;

    private String orderNote;
    private String promoCode;

    private String paymentMethod;

    private PaymentGatewayResponse gatewayResponse;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public ShippingAddressDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryContactNumber() {
        return primaryContactNumber;
    }

    public void setPrimaryContactNumber(String primaryContactNumber) {
        this.primaryContactNumber = primaryContactNumber;
    }

    public String getSecondaryContactNumber() {
        return secondaryContactNumber;
    }

    public void setSecondaryContactNumber(String secondaryContactNumber) {
        this.secondaryContactNumber = secondaryContactNumber;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public String getAddressTitle() {
        return addressTitle;
    }

    public void setAddressTitle(String addressTitle) {
        this.addressTitle = addressTitle;
    }

    public
    void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExtensionNumber() {
        return extensionNumber;
    }

    public void setExtensionNumber(String extensionNumber) {
        this.extensionNumber = extensionNumber;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public PaymentGatewayResponse getGatewayResponse() {
        return gatewayResponse;
    }

    public void setGatewayResponse(PaymentGatewayResponse gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }
}
