package com.octal.actorPay.controller;

import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.SubAdminDTO;
import com.octal.actorPay.dto.UserDTO;
import com.octal.actorPay.dto.request.SubAdminFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.pageResponse.PageUserResponse;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.AdminService;
import com.octal.actorPay.service.SubAdminService;

import com.octal.actorPay.specification.UserSpecification;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.time.LocalDateTime;


import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@Controller
@RestController
@RequestMapping("/subadmin")
public class SubAdminController extends PagedItemsController{

    @Autowired
    private AdminService adminService;

    @Autowired
    private SubAdminService subAdminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSpecification userSpecification;




    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody @Valid SubAdminDTO subAdminDTO, final HttpServletRequest request) {
        subAdminService.createSubAdmin(subAdminDTO, request.getHeader("userName"));
        return new ResponseEntity<>(new ApiResponse("SUCCESS: New sub admin created successfully.", null,
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/update/user")
    public ResponseEntity<?> updateUser(@Valid @RequestBody SubAdminDTO user, final HttpServletRequest request) {
        subAdminService.updateSubAdmin(user, request.getHeader("userName"));
        return new ResponseEntity<>(new ApiResponse("Sub admin updated successfully.",null,
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }

    @DeleteMapping("/delete/by/ids")
    public ResponseEntity<ApiResponse> deleteUserByIds(@RequestBody Map<String, List<String>> userIds, final HttpServletRequest request) throws InterruptedException {
        subAdminService.deleteSubAdmin(userIds.get("userIds"), request.getHeader("userName"));
        return new ResponseEntity<>(new ApiResponse("Sub Admin deleted successfully.",null,
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/by/id/{id}")
    public ResponseEntity getUserById(@PathVariable("id") String id, final HttpServletRequest request) {
        return ok(subAdminService.getSubAdminInfo(id, request.getHeader("userName")));
    }

    @PostMapping(value = "/get/all/paged")
    public ResponseEntity getAllUsersPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestParam(defaultValue = "createdAt") String sortBy,
                                           @RequestParam(defaultValue = "false") boolean asc,
                                           @RequestParam(defaultValue = "sub-admin", required = false) String userType,
                                           @RequestBody SubAdminFilterRequest subAdminFilterRequest,
                                           HttpServletRequest request) {

        String currentUser = request.getHeader("userName");
        /*if(StringUtils.isNotBlank(currentUser) && CommonConstant.USER_TYPE_SUB_ADMIN.equalsIgnoreCase(userType)) {
            subAdminFilterRequest.setEmail(currentUser);
        }*/

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);

        System.out.println(subAdminService.getAllSubAdminPaged(pagedInfo, currentUser ,subAdminFilterRequest ));
        return new ResponseEntity<>(new ApiResponse("All Sub-Admin users fetched Successfully.",
                subAdminService.getAllSubAdminPaged(pagedInfo, request.getHeader("userName"),subAdminFilterRequest),
                String.valueOf(HttpStatus.OK.value()),HttpStatus.OK),HttpStatus.OK);
    }

    @PutMapping("/change/status")
    public ResponseEntity<ApiResponse> changeSubAdminStatus(@RequestParam(name = "id") String id, @RequestParam(name = "status") Boolean status) throws InterruptedException {
        subAdminService.changeSubAdminStatus(id, status);
        return new ResponseEntity<>(new ApiResponse("Sub Category status updated successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }


    @GetMapping("/paginationSort/fetch")
    public ResponseEntity<Page<User>> findByKey(@RequestParam(defaultValue = "1") int pageNo,
                                                @RequestParam(defaultValue = "10") int pageSize,
                                                @RequestParam("firstName") String firstName, @RequestParam("contactNumber") String contactNumber,
                                                @RequestParam("email") String email, @RequestParam("isActive") boolean isActive,
                                                @RequestParam(defaultValue = "") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                @RequestParam(required = false /*defaultValue = "#{T(java.time.LocalDateTime).now()}"*/) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        LocalDateTime dateBetween = userRepository.findByCreatedAtBetween(startDate, endDate);
        UserDTO inpUser =  new UserDTO();
        inpUser.setFirstName(firstName);
        inpUser.setContactNumber(contactNumber);
        inpUser.setEmail(email);
        inpUser.setIsActive(isActive);
        inpUser.setCreatedAt(dateBetween);
        Map<String, Object> userWithPagination = subAdminService.findUserWithPaginationAndSorting(pageNo,pageSize);
        List<User> userWithKey = subAdminService.findUserWithKey(inpUser);

        return new ResponseEntity( new PageUserResponse("All Users fetched Successfully.", userWithKey, userWithPagination,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

}


