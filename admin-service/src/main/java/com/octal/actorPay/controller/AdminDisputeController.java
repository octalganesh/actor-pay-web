package com.octal.actorPay.controller;

import com.octal.actorPay.client.AdminUserService;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.DisputeMessageDTO;
import com.octal.actorPay.dto.request.DisputeFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ExceptionUtils;
import com.octal.actorPay.service.AdminService;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class AdminDisputeController {


    private AdminService adminService;
    private AdminUserService adminUserService;

    public AdminDisputeController(AdminService adminService, AdminUserService adminUserService) {
        this.adminService = adminService;
        this.adminUserService = adminUserService;
    }

    @Secured("ROLE_DISPUTE_SEND_MESSAGE")
    @PostMapping("/dispute/send/message")
    public ResponseEntity<ApiResponse> sendMessage(@Valid @RequestBody DisputeMessageDTO disputeMessageDTO,
                                                   HttpServletRequest request) {
        String userName = request.getHeader("userName");
        try {
            User user = adminService.getUserByEmailId(userName);
            String userId = user.getId();
            String email = user.getEmail();
            disputeMessageDTO.setPostedByName(email);
            disputeMessageDTO.setPostedById(userId);
            disputeMessageDTO.setUserType(CommonConstant.USER_TYPE_ADMIN);
            return adminUserService.sendMessage(disputeMessageDTO);
        } catch (FeignException feignException) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Error " + e.getMessage(), null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @Secured("ROLE_DISPUTE_UPDATE_BY_ID")
    @PutMapping("/dispute/update/{disputeId}")
    public ResponseEntity<ApiResponse> updateDispute(@PathVariable("disputeId") String disputeId,
                                                     @RequestParam(name = "status", required = false) String status,
                                                     @RequestParam(name = "penality", required = false) Double penality) {
        try {
            return adminUserService.updateDispute(disputeId, status, CommonConstant.USER_TYPE_ADMIN, penality);
        } catch (FeignException feignException) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Error " + e.getMessage(), null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @Secured("ROLE_DISPUTE_LIST_VIEW")
    @PostMapping("/dispute/list/paged")
    public ResponseEntity<ApiResponse> getAllDispute(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                     @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                     @RequestBody DisputeFilterRequest filterRequest,
                                                     HttpServletRequest request) {
        try {
            return adminUserService.getAllDispute(pageNo, pageSize, sortBy, asc, CommonConstant.USER_TYPE_ADMIN,filterRequest);
        } catch (FeignException feignException) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Error " + e.getMessage(), null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @Secured("ROLE_DISPUTE_VIEW_BY_ID")
    @GetMapping("/dispute/get/{disputeId}")
    public ResponseEntity<ApiResponse> getDisputeById(
            @PathVariable("disputeId") String disputeId,HttpServletRequest request) {
        String userName = request.getHeader("userName");
        User user = adminService.getUserByEmailId(userName);
        try {
            return adminUserService.getDisputeById(disputeId,user.getId(),CommonConstant.USER_TYPE_ADMIN);
        } catch (FeignException feignException) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Error " + e.getMessage(), null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }
}
