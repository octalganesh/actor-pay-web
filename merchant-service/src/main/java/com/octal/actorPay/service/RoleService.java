package com.octal.actorPay.service;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.RoleFilterRequest;
import com.octal.actorPay.entities.Permission;
import com.octal.actorPay.entities.RoleApiMapping;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RoleService {

    PageItem<RoleDTO> getAllRolesPaged(PagedItemInfo pagedInfo, RoleFilterRequest roleFilterRequest, String currentUser);

    RoleDTO getRoleById(String id, String actor);

    UserRoleResponse  createRole(RoleCreateRequest roleCreateRequest, String actor);

    void updateRole(RolePermissionUpdateRequest roleUpdateRequest, String actor);

    void deleteRoles(String roleId);

    void changeRoleStatus(String id, boolean status);

    List<RoleDTO> getAllRoles(String currentUser);

    List<Permission>  getAllPermission(String currentUser);

    RoleApiMappingResponse assignRoleToUser(RoleCreateRequest roleCreateRequest);

    UserRoleResponse getRoleByUserid(String id, String actor);

    UserRoleResponse addRoleToUser(String roleId, String userId);

    UserRoleResponse removeRoleFromUser(String roleId, String userId);
    List<RoleApiMapping> addDefaultRole();

    List<KeyValuePair> getRoleKeyValuePair(String currentUser);
}