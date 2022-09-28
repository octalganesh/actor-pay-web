package com.octal.actorPay.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "role")
public class Role extends AbstractPersistable {

    public enum RoleName {
        PRIMARY_MERCHANT,
        SUB_MERCHANT
    }

    @Column(name = "name", length = 30)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "user_id")
    private String userId;

}