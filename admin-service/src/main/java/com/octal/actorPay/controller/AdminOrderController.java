package com.octal.actorPay.controller;

import com.octal.actorPay.client.AdminOrderClient;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.AdminDTO;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.OrderNoteDTO;
import com.octal.actorPay.dto.request.OrderFilterRequest;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ExceptionUtils;
import com.octal.actorPay.service.AdminService;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/orders")
public class AdminOrderController {


    private AdminOrderClient adminOrderClient;

    private AdminService adminService;

    public AdminOrderController(AdminOrderClient adminOrderClient, AdminService adminService) {
        this.adminOrderClient = adminOrderClient;
        this.adminService = adminService;
    }

    @Secured("ROLE_ORDER_LIST_VIEW")
    @PostMapping("/list/paged")
    ResponseEntity<ApiResponse> viewAllOrder(@RequestParam(defaultValue = "0") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             @RequestParam(defaultValue = "createdAt") String sortBy,
                                             @RequestParam(defaultValue = "false") boolean asc,
                                             @RequestBody OrderFilterRequest filterRequest, HttpServletRequest request) {
        String userName = request.getHeader("userName");
        User user = adminService.getUserByEmailId(userName);
        filterRequest.setUserId(user.getId());
        ResponseEntity<ApiResponse> apiResponseResponseEntity
                = adminOrderClient.viewAllOrder(pageNo, pageSize, sortBy, asc, filterRequest);
        return apiResponseResponseEntity;
    }

    @Secured("ROLE_ORDER_VIEW")
    @GetMapping("/{orderNo}")
    ResponseEntity<ApiResponse> viewOrder(@PathVariable("orderNo") String orderNo)throws Exception {
        try {
            return adminOrderClient.viewOrder(orderNo,CommonConstant.USER_TYPE_ADMIN);
        } catch (
                FeignException feignException) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @Secured("ROLE_ORDER_CANCEL_BY_ORDERNO")
    @PostMapping("/cancel/{orderNo}")
    ResponseEntity<ApiResponse> cancelOrder(@PathVariable(name = "orderNo") String orderNo,
                                            @Valid @RequestPart(name = "cancelOrder") String cancelOrder,
                                            @RequestPart(name = "file", required = false) MultipartFile file,HttpServletRequest request)
            throws Exception {
        try {
            String adminEmail = request.getHeader("userName");
            User user  = adminService.getUserByEmailId(adminEmail);
            return adminOrderClient.cancelOrder(orderNo, cancelOrder, file, CommonConstant.USER_TYPE_ADMIN,user.getId());
        } catch (
                FeignException feignException) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @Secured("ROLE_ORDER_UPDATE_STATUS")
    @PutMapping("/status")
    ResponseEntity<ApiResponse> updateOrderStatus(@RequestParam("status") String status,
                                                  @RequestParam("orderNo") String orderNo,
                                                  @RequestBody(required = false) OrderNoteDTO orderNoteDTO,HttpServletRequest request)
            throws Exception {
        try {
            String userName = request.getHeader("userName");
            User user = adminService.getUserByEmailId(userName);
            return adminOrderClient.updateOrderStatus(status, orderNo, orderNoteDTO, CommonConstant.USER_TYPE_ADMIN,user.getId());
        } catch (FeignException feignException) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }
}
