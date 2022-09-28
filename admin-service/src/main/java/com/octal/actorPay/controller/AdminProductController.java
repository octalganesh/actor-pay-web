package com.octal.actorPay.controller;

import com.octal.actorPay.client.MerchantClient;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.request.ProductFilterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class AdminProductController {

    private MerchantClient merchantClient;

    @Autowired
    public AdminProductController(MerchantClient merchantClient) {
        this.merchantClient = merchantClient;
    }

    @Secured("ROLE_PRODUCT_VIEW_BY_ID")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable("productId") String productId) {
        ResponseEntity<ApiResponse> apiResponse = merchantClient.getProductById(productId);
        return apiResponse;
    }

    @Secured("ROLE_PRODUCT_LIST_VIEW")
    @PostMapping("/list/paged")
    public ResponseEntity<ApiResponse> getAllProduct(@RequestParam(name="pageNo",defaultValue = "0") Integer pageNo,
                                                     @RequestParam(name="pageSize",defaultValue = "10") Integer pageSize,
                                                     @RequestParam(name="sortBy",defaultValue = "createdAt") String sortBy,
                                                     @RequestParam(name="asc",defaultValue = "false") boolean asc,
                                                     @RequestBody ProductFilterRequest filterRequest) {
         ResponseEntity<ApiResponse> apiResponse = merchantClient.getAllProduct(pageNo,pageSize,sortBy,asc, filterRequest);
         return apiResponse;
    }

    @GetMapping("/active/{status}")
    public     ResponseEntity<ApiResponse> getAllProductByStatus(@RequestParam(name = "pageNo",defaultValue = "0") Integer pageNo,
                                                                 @RequestParam(name="pageSize", defaultValue = "10") Integer pageSize,
                                                                 @RequestParam(name="sortBy",defaultValue = "createdAt") String sortBy,
                                                                 @RequestParam(name="asc",defaultValue = "true") boolean asc,
                                                                 @PathVariable("status") Boolean status) {
        ResponseEntity<ApiResponse> apiResponse = merchantClient.getAllProductByStatus(pageNo, pageSize, sortBy, asc, status);
        return apiResponse;
    }

    @Secured("ROLE_PRODUCT_ACTIVE_STATUS")
    @PutMapping("/active/{status}")
    ResponseEntity<ApiResponse> updateProductStatus(@RequestParam("productId") List<String> productIds,
                                                    @PathVariable("status") Boolean status) {

        ResponseEntity<ApiResponse> apiResponse = merchantClient.updateProductStatus(productIds, status);
        return apiResponse;
    }

    @Secured("ROLE_PRODUCT_CHANGE_STATUS")
    @PutMapping("/change/status")
    ResponseEntity<ApiResponse> changeProductStatus(@RequestParam(name = "id") String id, @RequestParam(name = "status") Boolean status) {
        ResponseEntity<ApiResponse> apiResponse = merchantClient.changeProductStatus(id, status);
        return apiResponse;
    }
}

