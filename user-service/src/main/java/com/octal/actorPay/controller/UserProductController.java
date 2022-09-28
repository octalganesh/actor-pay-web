package com.octal.actorPay.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.request.ProductFilterRequest;
import com.octal.actorPay.feign.clients.MerchantClient;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class UserProductController {

    private MerchantClient merchantClient;

    @Autowired
    public UserProductController(MerchantClient merchantClient) {
        this.merchantClient = merchantClient;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable("productId") String productId) throws Exception{
        try {
            ResponseEntity<ApiResponse> apiResponse = merchantClient.getProductById(productId);
            String message = apiResponse.getBody().getMessage();
            return new ResponseEntity<>(new ApiResponse(message, apiResponse.getBody().getData(),
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } catch (FeignException e) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            String content = e.contentUTF8();
            Collection<ApiResponse> apiResponse = objectMapper.readValue(
                    content, new TypeReference<Collection<ApiResponse>>() { });
            System.out.println(apiResponse.stream().findFirst());
            System.out.println(e.getMessage());
            return new ResponseEntity<>(apiResponse.stream().findFirst().get(),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/list/paged")
    public ResponseEntity<ApiResponse> getAllProduct(@RequestParam(defaultValue = "0") Integer pageNo,
                                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                                     @RequestParam(defaultValue = "createdAt") String sortBy,
                                                     @RequestParam(defaultValue = "false") boolean asc,
                                                     @RequestBody ProductFilterRequest filterRequest) {
        ResponseEntity<ApiResponse> apiResponse = merchantClient.getAllProduct(pageNo, pageSize, sortBy, asc, filterRequest);
        return apiResponse;
    }

    @GetMapping(value = "/getProductName/{productId}")
    public ResponseEntity<String> getProductName(@PathVariable("productId") String productId) {
       return merchantClient.getProductName(productId);
    }
    @GetMapping("/merchantName/{merchantId}")
    public ResponseEntity<String> getMerchantName(@PathVariable("merchantId") String merchantId) {
        return merchantClient.getMerchantName(merchantId);
    }
}

