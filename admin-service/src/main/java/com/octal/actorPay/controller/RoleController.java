package com.octal.actorPay.controller;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.RoleDTO;
import com.octal.actorPay.dto.RolePermissionUpdate;
import com.octal.actorPay.dto.UpdateRoleRequest;
import com.octal.actorPay.dto.UserRoleResponse;
import com.octal.actorPay.dto.request.RoleFilterRequest;
import com.octal.actorPay.dto.request.SubAdminFilterRequest;
import com.octal.actorPay.entities.RoleAPIMapping;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.service.RoleService;
import com.octal.actorPay.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Author: Nancy Chauhan
 * -Handling of Role and permissions on all modules.
 * -Modules listing is a static list of sections in project on which role and permissions need to be defined.
 */
@RestController
@RequestMapping("/role")
public class RoleController  extends PagedItemsController{

    @Autowired
    private RoleService roleService;

    @GetMapping(value = "/get/all")
    public ResponseEntity getAllRolesPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(defaultValue = "createdAt") String sortBy,
                                          @RequestParam(defaultValue = "asc") boolean asc,
                                          HttpServletRequest request) {

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);

        return new ResponseEntity<>( new ApiResponse("All Roles",
                roleService.getRoleList(pagedInfo,request.getHeader("userName")) ,
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }
    //Ganesh
    @Secured("ROLE_LIST_VIEW")
    @PostMapping(value = "/get/all")
    public ResponseEntity getAllRolesPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(defaultValue = "createdAt") String sortBy,
                                          @RequestParam(defaultValue = "asc") boolean asc,
                                           @RequestBody RoleFilterRequest roleFilterRequest,
                                          HttpServletRequest request) {

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);

        return new ResponseEntity<>( new ApiResponse("All Roles",
                roleService.getRoleList(pagedInfo,request.getHeader("userName"),roleFilterRequest) ,
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_VIEW_BY_ID")
    @GetMapping(value = "/by/id")
    public ResponseEntity getRoleById(@RequestParam("id") String id, HttpServletRequest request) {
        return new ResponseEntity<>(new ApiResponse("Roles Data",
                roleService.getRoleById(id, request.getHeader("userName")),String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_CREATE")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createRole(@Valid @RequestBody RoleDTO roleDto, HttpServletRequest request) {
        roleService.createRole(roleDto, request.getHeader("userName"));
        return new ResponseEntity<>(new ApiResponse("Role created successfully with permissions",null,
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_UPDATE")
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody RolePermissionUpdate role, final HttpServletRequest request) {
        roleService.updateRole(role, request.getHeader("userName"));
        return new ResponseEntity<>(new ApiResponse("Role updated successfully.",null,
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_DELETE_BY_ID")
    @DeleteMapping("/delete/by/ids")
    public ResponseEntity<ApiResponse> deleteRole(@RequestBody Map<String, List<String>> roleIds,
                                                 HttpServletRequest request) throws InterruptedException {
        roleService.deleteRoles(roleIds);
        return new ResponseEntity<>(new ApiResponse("Role deleted successfully",null,
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/change/status")
    public ResponseEntity<?> changeStatus(@RequestParam(name = "id") String id, @RequestParam(name = "status") boolean status) {
        roleService.changeRoleStatus(id, status);
        return new ResponseEntity<>(new ApiResponse("Role status updated successfully.",null,
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/get/all/active")
    public ResponseEntity getAllSubCategories(HttpServletRequest request) {
        return new ResponseEntity<>(new ApiResponse("All active roles",
                roleService.getAllActiveRoles(), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_VIEW_PERMISSION")
    @GetMapping(value = "/get/permission")
    public ResponseEntity<ApiResponse> getAllPermission(HttpServletRequest request) {

        return new ResponseEntity<>(ResponseUtils.ActorPayResponse("All Available Roles list", roleService.getAllPermission( request.getHeader("userName")),
                HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value = "/addToUser")
    public ResponseEntity<ApiResponse> addRoleToUser(@RequestBody UpdateRoleRequest updateRoleRequest) {
        UserRoleResponse userRoleResponse = roleService.addRoleToUser(updateRoleRequest.getRoleId(), updateRoleRequest.getUserId());
        return new ResponseEntity<ApiResponse>(ResponseUtils.ActorPayResponse("Role Successfully Added ",
                userRoleResponse, HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value = "/removeFromUser")
    public ResponseEntity<ApiResponse> removeRoleFromUser(@RequestBody UpdateRoleRequest updateRoleRequest) {
        UserRoleResponse userRoleResponse = roleService.removeRoleFromUser(updateRoleRequest.getRoleId(), updateRoleRequest.getUserId());
        return new ResponseEntity<ApiResponse>(ResponseUtils.ActorPayResponse("Role Successfully Removed ",
                userRoleResponse, HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/role/default")
    public ResponseEntity<ApiResponse> defaultPermission() {
        List<RoleAPIMapping> roleApiMappings = roleService.addDefaultRole();
        return new ResponseEntity<ApiResponse>(ResponseUtils.ActorPayResponse("Role Successfully Removed ",
                roleApiMappings, HttpStatus.OK), HttpStatus.OK);
    }

}