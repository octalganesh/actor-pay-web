package com.octal.actorPay.dto;

import javax.validation.constraints.NotBlank;

/**
 * Author: Nancy
 * ModuleDTO mapped to modules entity
 */
public class ScreenDTO {
    private String id;
    @NotBlank
    private String name;
    private PermissionDTO permission;
    private Integer screenOrder;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PermissionDTO getPermission() {
        return permission;
    }

    public void setPermission(PermissionDTO permission) {
        this.permission = permission;
    }

    public Integer getScreenOrder() {
        return screenOrder;
    }

    public void setScreenOrder(Integer screenOrder) {
        this.screenOrder = screenOrder;
    }
}