package com.octal.actorPay.model.entities;

import com.octal.actorPay.entities.AbstractPersistable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "wallet")
public class Wallet extends AbstractPersistable {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "balance_amount", nullable = false)
    private Double balanceAmount = null;

    @Column(name = "user_type", nullable = false)
    private String userType;

    @Column(name = "is_deleted",columnDefinition = "boolean default false")
    private boolean deleted=false;

}