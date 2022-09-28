package com.octal.actorPay.controller;

import com.octal.actorPay.client.AdminFeignClient;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.exceptions.ExceptionUtils;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AdminCategoryController {

    private AdminFeignClient adminFeignClient;

    public AdminCategoryController(AdminFeignClient adminFeignClient) {
        this.adminFeignClient = adminFeignClient;
    }

    @Secured("ROLE_CATEGORY_LIST_VIEW")
    @GetMapping(value = "/get/all/categories")
    public ResponseEntity<ApiResponse> getAllCategories(
            @RequestParam(name = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(name = "asc", required = false, defaultValue = "false") Boolean asc,
            @RequestParam(name = "filterByIsActive", defaultValue = "true", required = false) Boolean filterByIsActive) throws Exception {
        try {
           return adminFeignClient.getAllCategories(sortBy, asc, filterByIsActive);
        } catch (
                FeignException fe) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(fe), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @Secured("ROLE_SUB_CATEGORY_LIST_VIEW")
    @GetMapping(value = "/get/all/subcategories/by/category")
    public ResponseEntity<ApiResponse> getAllActiveSubcategoryByCategoryId(
            @RequestParam("categoryId") String categoryId,
            @RequestParam(name = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(name = "asc", required = false, defaultValue = "false") Boolean asc,
            @RequestParam(name = "filterByIsActive", defaultValue = "true", required = false) Boolean filterByIsActive) throws Exception {
        try {
            return adminFeignClient.getAllActiveSubcategoryByCategoryId(categoryId, sortBy, asc, filterByIsActive);
//            return  new ResponseEntity<>( new ApiResponse("Tis is test",null,HttpStatus.OK.name(),HttpStatus.OK),HttpStatus.OK);
        } catch (
                FeignException fe) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(fe), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

}
