package com.octal.actorPay.service;

import com.octal.actorPay.dto.RoleApiMappingDTO;
import com.octal.actorPay.entities.Permission;
import com.octal.actorPay.entities.RoleApiMapping;

import java.util.ArrayList;
import java.util.List;

public interface RoleApiMappingService {

    void createRoleApiMapping(RoleApiMappingDTO roleApiMappingDTO, String actor);

    void updateRoleApiMapping(RoleApiMappingDTO roleApiMappingDTO, String actor);

    List<Permission> getAllPermission(String currentUser);

//    public List<RoleApiMapping> getAllRoleApiMapping();
    public ArrayList<RoleApiMapping> getRoleApiAssocByMapping(String roleId);


}
