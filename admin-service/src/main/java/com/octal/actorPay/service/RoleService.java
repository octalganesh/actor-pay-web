package com.octal.actorPay.service;

import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.RoleDTO;
import com.octal.actorPay.dto.RolePermissionUpdate;
import com.octal.actorPay.dto.UserRoleResponse;
import com.octal.actorPay.dto.request.RoleFilterRequest;
import com.octal.actorPay.entities.Permission;
import com.octal.actorPay.entities.RoleAPIMapping;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public interface RoleService {
    PageItem<RoleDTO> getRoleList(PagedItemInfo pagedInfo, String currentUser);
    PageItem<RoleDTO> getRoleList(PagedItemInfo pagedInfo, String currentUser, RoleFilterRequest roleFilterRequest);
    RoleDTO getRoleById(String id, String currentUser);
    void createRole(RoleDTO roleDto, String currentUser);
    void updateRole(RolePermissionUpdate role, String userName);
    void deleteRoles(Map<String, List<String>> roleIds);
    void changeRoleStatus(String id, boolean status);

    List<RoleDTO> getAllActiveRoles();

    List<Permission>  getAllPermission(String currentUser);

    UserRoleResponse addRoleToUser(String roleId, String userId);

    UserRoleResponse removeRoleFromUser(String roleId, String userId);

    List<RoleAPIMapping> addDefaultRole();

    ArrayList<RoleAPIMapping> getRoleApiAssocByMapping(String roleId);
}