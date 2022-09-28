package com.octal.actorPay.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class OfferFilterRequest implements Serializable {

    private String serviceName;
    private String offerTitle;
    private String offerDescription;
    private Float offerInPercentage;
    private Float offerInPrice;
    private Float maxDiscount;
    private Float minOfferPrice;
    private Float useLimit;
    private String promoCode;
    private String categories;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate offerStartDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate offerStartDateTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate offerEndDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate offerEndDateTo;

    private String offerType;
    private  Integer numberOfUsage;
    private Integer ordersPerDay;
    private Boolean active;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createdDateTo;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public Float getOfferInPercentage() {
        return offerInPercentage;
    }

    public void setOfferInPercentage(Float offerInPercentage) {
        this.offerInPercentage = offerInPercentage;
    }

    public Float getOfferInPrice() {
        return offerInPrice;
    }

    public void setOfferInPrice(Float offerInPrice) {
        this.offerInPrice = offerInPrice;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public LocalDate getOfferStartDateFrom() {
        return offerStartDateFrom;
    }

    public void setOfferStartDateFrom(LocalDate offerStartDateFrom) {
        this.offerStartDateFrom = offerStartDateFrom;
    }

    public LocalDate getOfferStartDateTo() {
        return offerStartDateTo;
    }

    public void setOfferStartDateTo(LocalDate offerStartDateTo) {
        this.offerStartDateTo = offerStartDateTo;
    }

    public LocalDate getOfferEndDateFrom() {
        return offerEndDateFrom;
    }

    public void setOfferEndDateFrom(LocalDate offerEndDateFrom) {
        this.offerEndDateFrom = offerEndDateFrom;
    }

    public LocalDate getOfferEndDateTo() {
        return offerEndDateTo;
    }

    public void setOfferEndDateTo(LocalDate offerEndDateTo) {
        this.offerEndDateTo = offerEndDateTo;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public Integer getNumberOfUsage() {
        return numberOfUsage;
    }

    public void setNumberOfUsage(Integer numberOfUsage) {
        this.numberOfUsage = numberOfUsage;
    }

    public Integer getOrdersPerDay() {
        return ordersPerDay;
    }

    public void setOrdersPerDay(Integer ordersPerDay) {
        this.ordersPerDay = ordersPerDay;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDate getCreatedDateFrom() {
        return createdDateFrom;
    }

    public void setCreatedDateFrom(LocalDate createdDateFrom) {
        this.createdDateFrom = createdDateFrom;
    }

    public LocalDate getCreatedDateTo() {
        return createdDateTo;
    }

    public void setCreatedDateTo(LocalDate createdDateTo) {
        this.createdDateTo = createdDateTo;
    }
}
