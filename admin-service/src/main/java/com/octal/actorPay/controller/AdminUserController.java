package com.octal.actorPay.controller;

import com.octal.actorPay.client.AdminUserService;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.UserDTO;
import com.octal.actorPay.dto.request.UserFilterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class AdminUserController extends PagedItemsController{

    @Autowired
    private AdminUserService adminUserService;

    @Secured("ROLE_USER_ADD")
    @PostMapping("/add/user")
    public ResponseEntity<?> createUser(@RequestBody @Valid UserDTO userDTO, final HttpServletRequest request) {
        adminUserService.addUser(userDTO);
        return new ResponseEntity<>(new ApiResponse("SUCCESS: New user created successfully.", null,
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }

    /**
     * update user information
     *
     * @param user    - userDTO object that contains user information
     * @param request - HttpServletRequest object
     */
    @Secured("ROLE_USER_UPDATE")
    @PutMapping("/update/user")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UserDTO user, final HttpServletRequest request) {
        return new ResponseEntity<>(adminUserService.updateUser(user), HttpStatus.OK);
    }

    @Secured("ROLE_USER_LIST_VIEW")
    @PostMapping(value = "/get/all/user/paged")
    public ResponseEntity<ApiResponse> getAllUsersPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                                        @RequestParam(defaultValue = "createdAt") String sortBy,
                                                        @RequestParam(defaultValue = "false") boolean asc,
                                                        @RequestBody UserFilterRequest userFilterRequest) throws Exception{

      // return new ResponseEntity(adminUserService.getAllUsersPaged(pageNo, pageSize, sortBy, asc), HttpStatus.OK);
        ResponseEntity<ApiResponse> responseEntity = adminUserService
                .getAllUsersPaged(pageNo, pageSize, sortBy, asc, userFilterRequest);
/*
        ResponseEntity<ApiResponse> responseEntity = adminUserService
                .getAllUsersPaged(pageNo, pageSize, sortBy, asc, CommonConstant.USER_TYPE_CUSTOMER, userFilterRequest);*/
        return responseEntity;

    }

    @Secured("ROLE_USER_DELETE_BY_ID")
    @DeleteMapping("/delete/by/ids")
    public ResponseEntity<ApiResponse> deleteUserByIds(@RequestBody Map<String, List<String>> userIds) {
        adminUserService.deleteUsersByIds(userIds);
        return new ResponseEntity<>(new ApiResponse("Users deleted successfully.",null,
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }

    /**
     * calling user-service user api
     * @param id
     * @return
     * @throws InterruptedException
     */
    @Secured("ROLE_USER_VIEW_BY_ID")
    @GetMapping("/get/details/by/id/{id}")
    public ResponseEntity<ApiResponse> getUserDetailsById(@PathVariable(name = "id") String id) throws InterruptedException {

        return new ResponseEntity<>(adminUserService.getUserById(id), HttpStatus.OK);
    }

    /**
     * user-service
     * @param userId
     * @return
     */
    @Secured("ROLE_USER_CHANGE_STATUS")
    @PutMapping("/change/status")
    public ResponseEntity<ApiResponse> changeUserStatus(@RequestParam(name = "id") String userId,
                                                        @RequestParam(name = "status") Boolean status) {
        return new ResponseEntity<>(adminUserService.changeUserStatus(userId,status), HttpStatus.OK);
    }
}