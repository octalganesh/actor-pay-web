package com.octal.actorPay.transformer;

import com.netflix.discovery.converters.Auto;
import com.octal.actorPay.dto.RoleDTO;
import com.octal.actorPay.dto.SubMerchantDTO;
import com.octal.actorPay.entities.Role;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.repositories.RoleRepository;
import com.octal.actorPay.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class SubMerchantTransformer {


    public static Function<User, SubMerchantDTO> SUB_MERCHANT_TO_DTO = (user) -> {

        SubMerchantDTO merchantDTO = new SubMerchantDTO();

        merchantDTO.setId(user.getId());
        merchantDTO.setContactNumber(user.getContactNumber());
        merchantDTO.setEmail(user.getEmail());
        merchantDTO.setExtensionNumber(user.getExtensionNumber());
        merchantDTO.setCreatedAt(user.getCreatedAt());
        merchantDTO.setFirstName(user.getFirstName());
        merchantDTO.setLastName(user.getLastName());
        merchantDTO.setGender(user.getGender());
        merchantDTO.setActive(user.isActive());
//        merchantDTO.setRoleId(allRoles.stream().findFirst().get().getId());
        return merchantDTO;
    };
}
