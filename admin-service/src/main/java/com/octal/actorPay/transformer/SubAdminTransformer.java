package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.SubAdminDTO;
import com.octal.actorPay.dto.UserDTO;
import com.octal.actorPay.entities.User;

import java.util.function.Function;

public class SubAdminTransformer {

    public static Function<User, SubAdminDTO> SUB_ADMIN_TO_DTO = (user) -> {

        SubAdminDTO userDTO = new SubAdminDTO();
        userDTO.setId(user.getId());
        userDTO.setContactNumber(user.getContactNumber());
        userDTO.setEmail(user.getEmail());
        userDTO.setExtensionNumber(user.getExtensionNumber());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setDateOfBirth(user.getDateOfBirth()!=null?user.getDateOfBirth().toString():null);
        userDTO.setAddress(user.getAddress());
        userDTO.setStatus(user.getActive());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setRoleId(user.getRole().getId());

//        userDTO.setPassword(user.getPassword());
//        userDTO.setRoles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toList()));
        return userDTO;
    };
}
