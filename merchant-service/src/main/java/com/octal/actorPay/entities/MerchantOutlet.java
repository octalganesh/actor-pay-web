package com.octal.actorPay.entities;

import lombok.Data;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "merchant_outlet")
public class MerchantOutlet extends AbstractPersistable {

    @Column(name = "merchant_id")
    private String merchantId;

    @Column(name = "outlet_id")
    private String outletId;


}
