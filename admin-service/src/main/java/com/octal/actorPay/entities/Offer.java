package com.octal.actorPay.entities;

import com.octal.actorPay.constants.OfferType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "offer")
public class Offer extends AbstractPersistable {

    @Column(name = "offer_title")
    private String offerTitle;

    @Column(name = "offer_description")
    private String offerDescription;

    @Column(name = "offer_in_percentage")
    private Float offerInPercentage;

    @Column(name = "offer_in_price")
    private Float offerInPrice;

    @Column(name = "offer_code")
    private String offerCode;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Categories categories;

    @Column(name = "offer_start_date")
    private LocalDateTime offerStartDate;

    @Column(name = "offer_end_date")
    private LocalDateTime offerEndDate;

    @Enumerated(EnumType.STRING)
    private OfferType OfferType;

    @Column(name = "max_discount")
    private Float maxDiscount;

    @Column(name = "min_offer_price")
    private Float minOfferPrice;

    @Column(name = "user_limit")
    private Float useLimit;

    @Column(name = "single_user_limit")
    private Float singleUserLimit;

  /*  @Column(name = "offer_type")
    private String offerType;*/

    @Column(name = "number_of_usage")
    private Integer numberOfUsage;

    @Column(name = "orders_per_day")
    private Integer ordersPerDay;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private String visibilityLevel;

    public String getVisibilityLevel() {
        return visibilityLevel;
    }

    public String getOfferCode() {
        return offerCode;
    }

    public void setOfferCode(String offerCode) {
        this.offerCode = offerCode;
    }

    public com.octal.actorPay.constants.OfferType getOfferType() {
        return OfferType;
    }

    public void setOfferType(com.octal.actorPay.constants.OfferType offerType) {
        OfferType = offerType;
    }

    /*    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }*/

    public void setVisibilityLevel(String visibilityLevel) {
        this.visibilityLevel = visibilityLevel;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Categories getCategories() {
        return categories;
    }

    public void setCategories(Categories categories) {
        this.categories = categories;
    }

    public LocalDateTime getOfferStartDate() {
        return offerStartDate;
    }

    public void setOfferStartDate(LocalDateTime offerStartDate) {
        this.offerStartDate = offerStartDate;
    }

    public LocalDateTime getOfferEndDate() {
        return offerEndDate;
    }

    public void setOfferEndDate(LocalDateTime offerEndDate) {
        this.offerEndDate = offerEndDate;
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

    public Float getMaxDiscount() {
        return maxDiscount;
    }

    public Float getSingleUserLimit() {
        return singleUserLimit;
    }

    public void setSingleUserLimit(Float singleUserLimit) {
        this.singleUserLimit = singleUserLimit;
    }

    public void setMaxDiscount(Float maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public Float getMinOfferPrice() {
        return minOfferPrice;
    }

    public void setMinOfferPrice(Float minOfferPrice) {
        this.minOfferPrice = minOfferPrice;
    }

    public Float getUseLimit() {
        return useLimit;
    }

    public void setUseLimit(Float useLimit) {
        this.useLimit = useLimit;
    }
}
