package com.octal.actorPay.entities;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "loyalty_rewards_history")
public class LoyaltyRewardsHistory extends AbstractPersistable {

    @Column(name = "user_id")
    private String userId;

    @Column(name = "reason")
    private String reason;

    @Column(name = "event")
    private String event;

    @Column(name = "reward_point")
    private String rewardPoint;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "type")
    private String type;

}
