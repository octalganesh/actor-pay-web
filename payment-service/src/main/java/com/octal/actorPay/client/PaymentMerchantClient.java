package com.octal.actorPay.client;

import com.octal.actorPay.configs.FeignSupportConfig;
import com.octal.actorPay.constants.EndPointConstants;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.ProductDTO;
import com.octal.actorPay.dto.SystemConfigurationDTO;
import com.octal.actorPay.dto.request.ProductFilterRequest;
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
public interface PaymentMerchantClient {

    @GetMapping(value = "/products/{productId}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getProductById(@PathVariable(name = "productId") String productId);

    @GetMapping(value = "/products/{productId}/test", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ProductDTO> getProductByIdTest(@PathVariable("productId") String productId);

//    @GetMapping("/products/active/{status}")
//    ResponseEntity<ApiResponse> getAllProductByStatus(@RequestParam(name = "pageNo",defaultValue = "0") Integer pageNo,
//                                                      @RequestParam(name="pageSize", defaultValue = "10") Integer pageSize,
//                                                      @RequestParam(name="sortBy",defaultValue = "createdAt") String sortBy,
//                                                      @RequestParam(name="asc",defaultValue = "true") boolean asc,
//                                                      @PathVariable("status") Boolean status);

    @PostMapping(value = "/products/list/paged", headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getAllProduct(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                              @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                              @RequestBody ProductFilterRequest filterRequest);

    @GetMapping(value = "/taxes/{taxId}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getTaxById(@PathVariable(name = "taxId") String taxId);

    @GetMapping(value = "/products/getProductName/{productId}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<String> getProductName(@PathVariable(name = "productId") String productId);

    @GetMapping(value = "/merchantName/{merchantId}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<String> getMerchantName(@PathVariable(name = "merchantId") String merchantId);

    @GetMapping(value = "/merchant/by/merchantId/{merchantId}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getMerchantByMerchantId(@PathVariable(name = "merchantId") String merchantId);
    @GetMapping(value = "/merchants/by/userId/{userId}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getMerchantById(@PathVariable(name = "userId") String userId);

    @GetMapping(value = "/merchantBusinessName/{businessName}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> findByMerchantBusinessName(@PathVariable(name = "businessName") String businessName);

    @GetMapping(value = "/products/byName/{productName}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> findByName(@PathVariable(name = "productName") String productName);

    @GetMapping(value = "/settings/key/{key}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getMerChantSettingsByKey(@PathVariable("key") String key,
                                                                @RequestParam("merchantId") String merchantId);

    @GetMapping(value = "/settings", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getMerchantSettings(@RequestParam("keys") List<String> keys,
                                                           @RequestParam("merchantId") String merchantId);

    @PutMapping(value = "/settings/add", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> addMerchantSettings(@RequestBody List<SystemConfigurationDTO> systemConfigurationDTOS,
                                                           @RequestParam("merchantId") String merchantId);

    @GetMapping(value = "/merchant/by/id/{id}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getMerchantUser(@PathVariable("id") String id);

    @GetMapping(value = "/get/user/{userIdentity}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getUserIdentity(@PathVariable("userIdentity") String userIdentity);
    @GetMapping(value = "/merchant/auth/details/by/email/{email}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getUserIdentityByEmail(@PathVariable("email") String userIdentity);
}