package com.octal.actorPay.entities;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Nancy
 * Roles can be created via admin panel.
 * Each role for example USER role have a static listing of roleToScreenMappings
 * On each module USER can have read or write permission.
 */

@Entity
@Table(name = "role")
public class Role extends AbstractPersistable {

    public enum RoleName {
        ADMIN,
        SUB_ADMIN
    }

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "user_id")
    private String userId;

    public Role() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}