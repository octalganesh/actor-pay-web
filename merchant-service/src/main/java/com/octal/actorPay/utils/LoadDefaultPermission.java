package com.octal.actorPay.utils;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.constants.ResourceType;
import com.octal.actorPay.dto.DefaultPermissions;
import com.octal.actorPay.entities.Permission;
import com.octal.actorPay.entities.Role;
import com.octal.actorPay.entities.RoleApiMapping;
import com.octal.actorPay.repositories.PermissionRepository;
import com.octal.actorPay.repositories.RoleApiMappingRepository;
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
    private RoleApiMappingRepository roleApiMappingRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @PostConstruct
    public void loadDefaultPermissions() {

        Role primaryMerchantRole = roleRepository.findByNameAndIsActiveTrue(ResourceType.PRIMARY_MERCHANT.name()).orElse(null);
        // Loading Primary Merchant Default Permission
//        List<RoleApiMapping> defaultPrimaryMerchantPermissions  = roleApiMappingRepository.findByRoleId(primaryMerchantRole.getId());
//        defaultPermissions = new DefaultPermissions();
//        HashMap<String, List<RoleApiMapping>> defaultPrimaryMerchantPermissionMap = new HashMap<>();
//        defaultPrimaryMerchantPermissionMap.put(CommonConstant.DEFAULT_PRIMARY_MERCHANT_PERMISSION,defaultPrimaryMerchantPermissions);
//        defaultPermissions.setPrimaryMerchantDefaultPermissions(defaultPrimaryMerchantPermissionMap);


        // Loading Submerchant Default Permission
        Role subMerchantRole = roleRepository.findByNameAndIsActiveTrue(ResourceType.SUB_MERCHANT.name()).orElse(null);
        List<RoleApiMapping> defaultSubMerchantPermissions  = roleApiMappingRepository.findByRoleId(subMerchantRole.getId());
        HashMap<String, List<RoleApiMapping>> defaultSubMerchantPermissionMap = new HashMap<>();
        defaultSubMerchantPermissionMap.put(CommonConstant.DEFAULT_SUB_MERCHANT_PERMISSION,defaultSubMerchantPermissions);
        defaultPermissions.setSubMerchantDefaultPermissions(defaultSubMerchantPermissionMap);
    }

    public DefaultPermissions getDefaultPermissions() {
        return defaultPermissions;
    }
}
