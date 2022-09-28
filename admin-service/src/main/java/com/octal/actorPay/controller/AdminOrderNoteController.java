package com.octal.actorPay.controller;

import com.octal.actorPay.client.AdminUserService;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.OrderNoteDTO;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ExceptionUtils;
import com.octal.actorPay.service.AdminService;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AdminOrderNoteController {

    private AdminUserService adminUserService;

    private AdminService adminService;

    public AdminOrderNoteController(AdminUserService adminUserService, AdminService adminService) {
        this.adminUserService = adminUserService;
        this.adminService = adminService;
    }

    @Secured("ROLE_ORDERNOTE_ADD_POST")
    @PostMapping("/orderNotes/post")
    ResponseEntity<ApiResponse> addOrderNote(@RequestBody OrderNoteDTO orderNoteDTO,
                                             HttpServletRequest request) {
        String loggedInUser = request.getHeader("userName");
        User user = adminService.getUserByEmailId(loggedInUser);

        try {
            ResponseEntity<ApiResponse> responseEntity =
                    adminUserService.addOrderNote(orderNoteDTO, CommonConstant.USER_TYPE_ADMIN, user.getId(), user.getEmail());
            return responseEntity;
        } catch (FeignException fe) {
            ApiResponse apiResponse = ExceptionUtils.parseFeignExceptionMessage(fe);
            return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(String.format(
                    "Error while creating Order Note ,%s", e.getMessage()),
                    null, String.valueOf(HttpStatus.OK.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(String.format(
                    "Error while creating Order Note ,%s", e.getMessage()),
                    null, String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }

    }

    @Secured("ROLE_ORDERNOTE_LIST_VIEW")
    @GetMapping("/orderNotes/list/paged")
    ResponseEntity<ApiResponse> getAllOrderNote(@RequestParam(name = "orderNo",required = false) String orderNo,
                                                HttpServletRequest request) {
        String loggedInUser = request.getHeader("userName");
        User user = adminService.getUserByEmailId(loggedInUser);
        try {
            ResponseEntity<ApiResponse> apiResponseResponseEntity = adminUserService
                    .getAllOrderNote(orderNo,CommonConstant.USER_TYPE_ADMIN);
            if (apiResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
                ApiResponse apiResponse = apiResponseResponseEntity.getBody();
                return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<ApiResponse>(new ApiResponse("Error whlie getting Order Note ", null,
                        String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST),
                        HttpStatus.BAD_REQUEST);
            }
        } catch (FeignException fe) {
            ApiResponse apiResponse = ExceptionUtils.parseFeignExceptionMessage(fe);
            return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(String.format(
                    "Error while creating Order Note ,%s", e.getMessage()),
                    null, String.valueOf(HttpStatus.OK.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<ApiResponse>(new ApiResponse(String.format(
                    "Error while creating Order Note ,%s", e.getMessage()),
                    null, String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }
}
