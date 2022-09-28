package com.octal.actorPay.config;

import com.amazonaws.services.workdocs.model.RoleType;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.ResourceType;
import com.octal.actorPay.dto.DefaultPermissions;
import com.octal.actorPay.entities.Permission;
import com.octal.actorPay.entities.Role;
import com.octal.actorPay.entities.RoleAPIMapping;
import com.octal.actorPay.repositories.PermissionRepository;
import com.octal.actorPay.repositories.RoleAPIMappingRepository;
import com.octal.actorPay.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

@Component
public class LoadDefaultPermission {

    private List<Permission> permissions;


    @Autowired
    private DefaultPermissions defaultPermissions;

    @Autowired
    private RoleAPIMappingRepository roleApiMappingRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @PostConstruct
    public void loadDefaultPermissions() {

        Role adminRole = roleRepository.findByNameAndIsActiveTrue(Role.RoleName.ADMIN.name()).orElse(null);

//        // Loading sub-admin Default Permission
        Role subAdminRole = roleRepository.findByNameAndIsActiveTrue(Role.RoleName.SUB_ADMIN.name()).orElse(null);
        List<RoleAPIMapping> defaultSubMerchantPermissions  = roleApiMappingRepository.findByRoleId(subAdminRole.getId());
        HashMap<String, List<RoleAPIMapping>> defaultSubMerchantPermissionMap = new HashMap<>();
        defaultSubMerchantPermissionMap.put(CommonConstant.DEFAULT_SUB_MERCHANT_PERMISSION,defaultSubMerchantPermissions);
        defaultPermissions.setSubAdminDefaultPermissions(defaultSubMerchantPermissionMap);
    }

    public DefaultPermissions getDefaultPermissions() {
        return defaultPermissions;
    }
}

