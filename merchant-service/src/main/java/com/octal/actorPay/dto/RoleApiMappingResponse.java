package com.octal.actorPay.dto;

import com.octal.actorPay.entities.AbstractPersistable;
import com.octal.actorPay.entities.Permission;
import lombok.Data;

import java.util.List;

@Data
public class RoleApiMappingResponse extends AbstractPersistable {

    private RoleDTO roleDTO;

    private List<Permission> permissionList;

    private UserDTO userDTO;

}
