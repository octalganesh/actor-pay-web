package com.octal.actorPay.model.entities;

import com.octal.actorPay.constants.PurchaseStatus;
import com.octal.actorPay.entities.AbstractPersistable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "purchase_details")
public class PurchaseDetails extends AbstractPersistable {

    @Column(name = "parent_transaction")
    private String parentTransaction;

    @Column(name = "order_no")
    private String orderNo;

    @Enumerated(EnumType.STRING)
    private PurchaseStatus purchaseStatus;

}


