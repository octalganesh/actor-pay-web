package com.octal.actorPay.model.entities;

import com.octal.actorPay.entities.AbstractPersistable;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "pg_details")
@Data
public class PgDetails extends AbstractPersistable {

    @Column(name = "payment_id",nullable = false)
    private String paymentId;

    @Column(name = "pg_order_id")
    private String pgOrderId;

    @Column(name = "pg_signature")
    private String pgSignature;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_type_id")
    private String paymentTypeId;

}
