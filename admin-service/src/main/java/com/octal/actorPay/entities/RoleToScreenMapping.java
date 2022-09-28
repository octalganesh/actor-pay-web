package com.octal.actorPay.entities;

import javax.persistence.*;

/**
 * This is entity class for ROLE and Module mapping for corresponding permissions
 * Module and Permissions has one to one relation
 * Role and module has many to one relation
 */
//@Entity
//@Table(name = "role_to_screen_mapping")
public class RoleToScreenMapping extends AbstractPersistable {

//    @ManyToOne
//    @JoinColumn(name = "role_id")
//    private Role role;
//
//    @OneToOne
//    @JoinColumn(name = "screen_id")
//    private Screens screen;
//
//    @OneToOne(mappedBy = "roleToScreenMapping", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private Permission permission;
//
//    public RoleToScreenMapping() {
//    }
//
//    public Role getRole() {
//        return role;
//    }
//
//    public void setRole(Role role) {
//        this.role = role;
//    }
//
//    public Permission getPermission() {
//        return permission;
//    }
//
//    public void setPermission(Permission permission) {
//        this.permission = permission;
//    }
//
//    public Screens getScreen() {
//        return screen;
//    }
//
//    public void setScreen(Screens screen) {
//        this.screen = screen;
//    }
}