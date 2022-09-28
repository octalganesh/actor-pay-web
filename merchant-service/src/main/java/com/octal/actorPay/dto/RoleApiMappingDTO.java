package com.octal.actorPay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.octal.actorPay.entities.Permission;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class RoleApiMappingDTO {

    private String roleId;

    private String permissionId;

    private String permissionName;


}
