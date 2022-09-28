package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.ScreenDTO;
import com.octal.actorPay.dto.PermissionDTO;
import com.octal.actorPay.dto.RoleDTO;
import com.octal.actorPay.entities.*;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Author: Nancy
 * Provides the mapping of entity with DTO
 * Provides the mapping of role and permissions
 */
public class RoleTransformer {

    public static final Function< Role, RoleDTO> ROLE_TO_DTO = (role) -> {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName());
        roleDTO.setDescription(role.getDescription());
        roleDTO.setCreatedAt(role.getCreatedAt());
        roleDTO.setUpdatedAt(role.getUpdatedAt());
        roleDTO.setActive(role.getActive());
        roleDTO.setUserId(role.getUserId());

        return roleDTO;
    };
}