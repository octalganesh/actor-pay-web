package com.octal.actorPay.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "ROLE_API_MAPPING")
@Data
public class RoleAPIMapping extends AbstractPersistable {

    private String roleId;

    private String permissionId;

    @Transient
    private String permissionName;

}
