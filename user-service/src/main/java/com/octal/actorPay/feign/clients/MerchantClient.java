package com.octal.actorPay.feign.clients;

import com.octal.actorPay.configs.FeignSupportConfig;
import com.octal.actorPay.constants.EndPointConstants;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.ProductDTO;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import com.octal.actorPay.dto.request.ProductFilterRequest;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.octal.actorPay.constants.EndPointConstants.MerchantServiceConstants.MERCHANT_BASE_URL;

@FeignClient(name = EndPointConstants.MerchantServiceConstants.MERCHANT_MICROSERVICE,
        url = MERCHANT_BASE_URL,
        configuration = FeignSupportConfig.class)
@Service
public interface MerchantClient {


    @GetMapping(path = "/products/{productId}",headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getProductById(
            @PathVariable(name = "productId") String productId);

    @GetMapping(path = "/products/{productId}/test",headers = {"username=admin@yopmail.com"})
    ResponseEntity<ProductDTO> getProductByIdTest(@PathVariable("productId") String productId);

    @PostMapping(path = "/products/list/paged",headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getAllProduct(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                              @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                              @RequestBody ProductFilterRequest filterRequest);

    @GetMapping(path = "/taxes/{taxId}",headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getTaxById(@PathVariable(name = "taxId") String taxId);

    @GetMapping(path = "/products/getProductName/{productId}",headers = {"username=admin@yopmail.com"})
    ResponseEntity<String> getProductName(@PathVariable(name = "productId") String productId);

    @GetMapping(path = "/merchantName/{merchantId}",headers = {"username=admin@yopmail.com"})
    ResponseEntity<String> getMerchantName(@PathVariable(name = "merchantId") String merchantId);

    @GetMapping(path = "/merchant/by/merchantId/{merchantId}",headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getMerchantByMerchantId(@PathVariable(name = "merchantId") String merchantId);

    @GetMapping(path = "/merchantBusinessName/{businessName}",headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> findByMerchantBusinessName(@PathVariable(name = "businessName") String businessName);

    @GetMapping(path = "/products/byName/{productName}",headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> findByName(@PathVariable(name = "productName") String productName);

    @GetMapping(path = "/settings/key/{key}",headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getMerChantSettingsByKey(@PathVariable("key") String key,
                                                         @RequestParam("merchantId") String merchantId);

    @GetMapping(path = "/settings",headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getMerchantSettings(@RequestParam("keys") List<String> keys,
                                                    @RequestParam("merchantId") String merchantId);

    @PutMapping(path = "/settings/add",headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> addMerchantSettings(@RequestBody List<SystemConfigurationDTO> systemConfigurationDTOS,
                                                    @RequestParam("merchantId") String merchantId);

    @GetMapping(path = "/merchants/by/userId/{userId}",headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getMerchantByUserId(@PathVariable(name = "userId") String userId);

    @GetMapping(path = "/get/user/{userIdentity}",headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getUserIdentity(@PathVariable("userIdentity") String userIdentity);

    @PutMapping(path = "/products/update/product/stock",headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> updateProductStock(
            @RequestParam(name = "productId") String productId,
            @RequestParam(name = "stockCount") Integer stockCount,
            @RequestParam(name = "stockStatus") String stockStatus);
}