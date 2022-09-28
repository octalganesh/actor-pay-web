package com.octal.actorPay.controller;

import com.octal.actorPay.feign.clients.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserCategoryController {

    private AdminClient adminClient;

    public UserCategoryController(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    @GetMapping(value = "/get/all/active/categories")
    public ResponseEntity getAllActiveCategories() {
        return new ResponseEntity<>(adminClient.getAllActiveCategories(), HttpStatus.OK);
    }

    @GetMapping(value = "/get/all/categories/paged")
    public ResponseEntity getAllCategoriesPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                @RequestParam(defaultValue = "createdAt") String sortBy,
                                                @RequestParam(defaultValue = "false") boolean asc,
                                                @RequestParam(name = "filterByName", required = false) String filterByName,
                                                @RequestParam(name = "filterByIsActive", required = false) Boolean filterByIsActive) {
        return new ResponseEntity<>(adminClient.getAllCategoriesPaged(pageNo, pageSize, sortBy, asc, filterByName, filterByIsActive), HttpStatus.OK);
    }

    @GetMapping(value = "/get/all/subcategories/paged")
    public ResponseEntity getAllSubCategoriesPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestParam(defaultValue = "createdAt") String sortBy,
                                                   @RequestParam(defaultValue = "false") boolean asc,
                                                   @RequestParam(name = "filterByName", required = false) String filterByName,
                                                   @RequestParam(name = "filterByIsActive", required = false) Boolean filterByIsActive,
                                                   @RequestParam(name = "filterByCategoryName", required = false) String filterByCategoryName) {
        return new ResponseEntity<>(adminClient.getAllSubCategoriesPaged(pageNo, pageSize, sortBy, asc, filterByName,
                filterByIsActive,filterByCategoryName), HttpStatus.OK);
    }

    @GetMapping(value = "/get/all/subcategories/by/category")
    public ResponseEntity getAllActiveSubCategories(@RequestParam("categoryId") String categoryId) {
        return new ResponseEntity<>(adminClient.getAllActiveSubCategories(categoryId), HttpStatus.OK);
    }
}
