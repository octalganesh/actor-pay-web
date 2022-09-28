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
@Table(name = "wallet_commission_percentage")
public class WalletCommissionPercentage extends AbstractPersistable {
    
    @Column(name = "commission_percentage_id",nullable = false)
    private String commissionPercentageId;

    @Column(name = "percentage_value",nullable = false)
    private double percentageValue;

    public String getCommissionPercentageId() {
        return commissionPercentageId;
    }

    public void setCommissionPercentageId(String commissionPercentageId) {
        this.commissionPercentageId = commissionPercentageId;
    }

    public double getPercentageValue() {
        return percentageValue;
    }

    public void setPercentageValue(double percentageValue) {
        this.percentageValue = percentageValue;
    }
}
