package com.octal.actorPay.controller;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.RoleFilterRequest;
import com.octal.actorPay.entities.RoleApiMapping;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.service.RoleApiMappingService;
import com.octal.actorPay.service.RoleService;
import com.octal.actorPay.utils.CommonUtils;
import com.octal.actorPay.utils.ResponseUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
public class RoleController extends BaseController {

    private static final Logger logger = LogManager.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleApiMappingService roleApiMappingService;

    @Secured("ROLE_VIEW_BY_ID")
    @GetMapping(value = "/role/by/id")
    public ResponseEntity<ApiResponse> getRoleById(@RequestParam("id") String id, HttpServletRequest httpServletRequest) {
        User user = getAuthorizedUser(httpServletRequest);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Role and Permission Details ", roleService.getRoleById(id, user.getEmail())
                , HttpStatus.OK), HttpStatus.OK);
    }

    //    @Secured("ROLE_VIEW_BY_USER_ID")
    @GetMapping(value = "/role/by/user")
    public ResponseEntity<ApiResponse> getRoleByUserId(@RequestParam("userId") String userId, HttpServletRequest httpServletRequest) {
        User user = getAuthorizedUser(httpServletRequest);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("User Role and Permission Details ",
                roleService.getRoleByUserid(userId, user.getEmail())
                , HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_CREATE")
    @PostMapping("/role/create")
    public ResponseEntity<ApiResponse> createRole(@Valid @RequestBody RoleCreateRequest roleCreateRequest, HttpServletRequest httpServletRequest) {
        User user = getAuthorizedUser(httpServletRequest);
        roleService.createRole(roleCreateRequest, user.getEmail());
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Role created successfully ", null
                , HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_UPDATE")
    @PutMapping("/role/update")
    public ResponseEntity<?> updateRole(@Valid @RequestBody RolePermissionUpdateRequest roleUpdateRequest, final HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        roleService.updateRole(roleUpdateRequest, user.getEmail());
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Role updated successfully ", null
                , HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_DELETE_BY_ID")
    @DeleteMapping("/role/delete/by/id")
    public ResponseEntity<ApiResponse> deleteRole(@RequestParam("roleId") String roleId,
                                                  HttpServletRequest request) {
        roleService.deleteRoles(roleId);
        return new ResponseEntity<>(new ApiResponse("Role deleted successfully", "",
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/role/change/status")
    public ResponseEntity<?> changeStatus(@RequestParam(name = "id") String id, @RequestParam(name = "status") boolean status) {
        roleService.changeRoleStatus(id, status);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Role status updated successfully", null
                , HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_LIST_VIEW")
    @PostMapping(value = "/roles/list/paged")
    public ResponseEntity getAllUsersPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "createdAt") String sortBy,
                                           @RequestParam(defaultValue = "false") boolean asc,
                                           @RequestBody RoleFilterRequest roleFilterRequest,
                                           HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Roles list", roleService.getAllRolesPaged(pagedInfo, roleFilterRequest, user.getEmail()),
                HttpStatus.OK), HttpStatus.OK);
    }

    //@Secured("ROLE_LIST_VIEW")
    @GetMapping(value = "/role-key-value")
    public ResponseEntity<?> roleKeyValuePair(HttpServletRequest request) throws Exception {
        logger.info("RoleController.roleKeyValuePair");
        try {
            User user = getAuthorizedUser(request);

            return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Roles list", roleService.getRoleKeyValuePair(user.getEmail()),
                    HttpStatus.OK), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(),
                    null, String.valueOf(101), HttpStatus.OK), HttpStatus.OK);
        }
    }

/*    //@Secured("ROLE_LIST_VIEW")
    @GetMapping(value = "/roles/lists")
    public ResponseEntity getAllUsers( HttpServletRequest request) {
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Roles list", roleService.getAllRolesPaged(null, null, CommonUtils.getCurrentUser(request)),
                HttpStatus.OK), HttpStatus.OK);
    }*/

    @GetMapping(value = "/role/get/all")
    public ResponseEntity getAllUsersPaged(HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("Roles list", roleService.getAllRoles(user.getEmail()),
                HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value = "/role/addToUser")
    public ResponseEntity<ApiResponse> addRoleToUser(@RequestBody UpdateRoleRequest updateRoleRequest) {
        UserRoleResponse userRoleResponse = roleService.addRoleToUser(updateRoleRequest.getRoleId(), updateRoleRequest.getUserId());
        return new ResponseEntity<ApiResponse>(ResponseUtils.ActorPayResponse("Role Successfully Added ",
                userRoleResponse, HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value = "/role/removeFromUser")
    public ResponseEntity<ApiResponse> removeRoleFromUser(@RequestBody UpdateRoleRequest updateRoleRequest) {
        UserRoleResponse userRoleResponse = roleService.removeRoleFromUser(updateRoleRequest.getRoleId(), updateRoleRequest.getUserId());
        return new ResponseEntity<ApiResponse>(ResponseUtils.ActorPayResponse("Role Successfully Removed ",
                userRoleResponse, HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/role/default")
    public ResponseEntity<ApiResponse> defaultPermission() {
        List<RoleApiMapping> roleApiMappings = roleService.addDefaultRole();
        return new ResponseEntity<ApiResponse>(ResponseUtils.ActorPayResponse("Role Successfully Removed ",
                roleApiMappings, HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_VIEW_PERMISSION")
    @GetMapping(value = "/role/get/permission")
    public ResponseEntity<ApiResponse> getAllPermission(HttpServletRequest request) {
        User user = getAuthorizedUser(request);
        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("All Available Roles list", roleService.getAllPermission(user.getEmail()),
                HttpStatus.OK), HttpStatus.OK);
    }
}