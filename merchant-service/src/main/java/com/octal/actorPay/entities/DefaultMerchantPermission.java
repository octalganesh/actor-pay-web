package com.octal.actorPay.entities;

import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "default_merchant_permission")
public class DefaultMerchantPermission {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column(name = "permission_id")
    private String permissionId;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "name")
    private String name;


}
