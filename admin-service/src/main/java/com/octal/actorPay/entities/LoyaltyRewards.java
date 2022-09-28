package com.octal.actorPay.entities;

import com.octal.actorPay.constants.Event;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "loyalty_rewards")
public class LoyaltyRewards extends AbstractPersistable {

    @Column(name = "event")
    @Enumerated(EnumType.STRING)
    Event event;
    @Column(name = "reward_point")
    Long rewardPoint;
    @Column(name = "price_limit")
    float priceLimit;
    @Column(name = "single_user_limit")
    float singleUserLimit;

}
