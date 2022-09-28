package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.RoleDTO;
import com.octal.actorPay.entities.Role;

import java.util.function.Function;

public class RoleTransformer {

    public static final Function<Role, RoleDTO> ROLE_TO_DTO = (role) -> {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        roleDTO.setDescription(role.getDescription());
        roleDTO.setCreatedAt(role.getCreatedAt());
        roleDTO.setUpdatedAt(role.getUpdatedAt());
        roleDTO.setActive(role.isActive());
        roleDTO.setUserId(role.getUserId());

        return roleDTO;
    };
}