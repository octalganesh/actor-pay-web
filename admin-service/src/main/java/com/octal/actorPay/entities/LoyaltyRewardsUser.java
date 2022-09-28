package com.octal.actorPay.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "loyalty_rewards_user")
public class LoyaltyRewardsUser extends AbstractPersistable {

    @Column(name = "user_id")
    private String userId;

    @Column(name = "total_rewards")
    private Long totalRewards = 0l;
}
