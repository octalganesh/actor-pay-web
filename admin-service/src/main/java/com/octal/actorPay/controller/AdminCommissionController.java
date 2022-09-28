package com.octal.actorPay.controller;

import com.octal.actorPay.client.AdminUserService;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.request.ProductCommissionFilterRequest;
import com.octal.actorPay.exceptions.ExceptionUtils;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class AdminCommissionController {

    private AdminUserService adminUserService;


    public AdminCommissionController(AdminUserService adminUserService) {
        this.adminUserService=adminUserService;
    }

    @Secured("ROLE_COMMISSION_LIST_VIEW")
    @PostMapping("/productCommission/list/paged")
    public ResponseEntity<ApiResponse> getProductAllCommissions(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                                @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                                @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                                @RequestBody ProductCommissionFilterRequest filterRequest,
                                                                HttpServletRequest request) throws Exception {
        try {
            ResponseEntity<ApiResponse> responseEntity = adminUserService.getProductAllCommissions(pageNo, pageSize, sortBy, asc, filterRequest);
            return responseEntity;
        } catch (FeignException feignException) {
            ExceptionUtils.parseFeignExceptionMessage(feignException);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error: Contact Administrator " + e.getMessage(),
                    null, HttpStatus.BAD_REQUEST.name(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    @Secured("ROLE_COMMISSION_UPDATE")
    @PutMapping("/productCommission/update")
    ResponseEntity<ApiResponse> updateProductCommission(@RequestParam("status") String settlementStatus,@RequestParam("ids") List<String> ids) throws Exception {
        try {
            ResponseEntity<ApiResponse> responseEntity = adminUserService.updateProductCommission(settlementStatus,ids);
            return responseEntity;
        } catch (FeignException feignException) {
           return new ResponseEntity<ApiResponse>(ExceptionUtils.parseFeignExceptionMessage(feignException),HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error: Contact Administrator " + e.getMessage(),
                    null, HttpStatus.BAD_REQUEST.name(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }
}
