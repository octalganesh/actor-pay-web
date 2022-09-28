package com.octal.actorPay.dto;

import com.octal.actorPay.entities.RoleAPIMapping;
import com.octal.actorPay.entities.Screens;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Nancy
 * RoleDTO mapped with Role entity
 */
public class RoleDTO extends BaseDTO{

    private String id;

    private String userId;

    @NotBlank
    @Size(max = 255)
    private String name;
    @Size(max = 500)
    private String description;

    private ArrayList<String> permissions;

    private List<RoleAPIMapping> roleApiMappings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(ArrayList<String> permissions) {
        this.permissions = permissions;
    }

    public List<RoleAPIMapping> getRoleApiMappings() {
        return roleApiMappings;
    }

    public void setRoleApiMappings(List<RoleAPIMapping> roleApiMappings) {
        this.roleApiMappings = roleApiMappings;
    }
}