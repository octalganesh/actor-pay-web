package com.octal.actorPay.entities;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Nancy
 * Roles can be created via admin panel.
 * Each role for example USER role have a static listing of roleToScreenMappings
 * On each module USER can have read or write permission.
 */

@Entity
@Table(name = "referral_setting")
public class ReferralSetting extends AbstractPersistable {

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "order_number")
    private Integer orderNumber;

    @Column(name = "min_order_value")
    private Integer minOrderValue;

    @Column(name = "max_discount")
    private Integer maxDiscount;

    @Column(name = "discount_in_price")
    private float discountInPrice = 0.0f;

    @Column(name = "discount_in_percentage")
    private float discountInPercentage = 0.0f;

    @Column(name = "created_by")
    private String createdBy;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(Integer minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public Integer getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(Integer maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public float getDiscountInPrice() {
        return discountInPrice;
    }

    public void setDiscountInPrice(float discountInPrice) {
        this.discountInPrice = discountInPrice;
    }

    public float getDiscountInPercentage() {
        return discountInPercentage;
    }

    public void setDiscountInPercentage(float discountInPercentage) {
        this.discountInPercentage = discountInPercentage;
    }
}