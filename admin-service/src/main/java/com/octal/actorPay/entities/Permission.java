package com.octal.actorPay.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Author: Nancy
 * Permissions is assigned to some module
 */
@Entity
@Table(name = "permission")
public class Permission extends AbstractPersistable {

    @Column(name = "name", unique = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}