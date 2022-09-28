package com.octal.actorPay.dto;

import lombok.Data;

import java.util.List;

@Data
public class RolePermissionUpdate extends RoleDTO{

    private List<String> addPermissionIds;
    private List<String> removePermissionIds;
}
