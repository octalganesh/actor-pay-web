package com.octal.actorPay.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "permission")
public class Permission extends AbstractPersistable {

    @Column(name = "name", unique = true)
    private String name;


}