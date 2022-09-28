package com.octal.actorPay.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "promo_code_history")
public class PromoCodesHistory extends AbstractPersistable {

    @Column(name = "user_id")
    private String userId;

    @Column(name = "promo_code")
    private String promoCode;

    @Column(name = "used_count")
    private String usedCount;

    @Column(name = "discount_amount")
    private float discountAmount;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "order_item_id")
    private String orderItemId;
}
