package com.octal.actorPay.dto;

import com.octal.actorPay.entities.RoleApiMapping;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserRoleResponse implements Serializable {

    private UserDTO userDTO;

    private RoleDTO roleDTO;
}
