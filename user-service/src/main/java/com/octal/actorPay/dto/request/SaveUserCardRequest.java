package com.octal.actorPay.dto.request;

import javax.validation.constraints.NotBlank;

public class SaveUserCardRequest {

    private String id;
    @NotBlank
    private String cardNumber;
    @NotBlank
    private String cardType;
    @NotBlank
    private String month;
    @NotBlank
    private String year;
    private String cardFormat;
    @NotBlank
    private String userId;


    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCardFormat() {
        return cardFormat;
    }

    public void setCardFormat(String cardFormat) {
        this.cardFormat = cardFormat;
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
}
