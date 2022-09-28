package com.octal.actorPay.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "outlet_permission")
public class OutletPermission extends  AbstractPersistable{

    @Column(name = "permission_id")
    private String permissionId;

    @Column(name = "outlet_id")
    private String outletId;

}
