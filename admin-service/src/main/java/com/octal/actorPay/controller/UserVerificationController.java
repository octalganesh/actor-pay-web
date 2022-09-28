package com.octal.actorPay.controller;

import com.google.gson.Gson;
import com.octal.actorPay.client.AdminUserService;
import com.octal.actorPay.client.MerchantClient;
import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.EkycFilterRequest;
import com.octal.actorPay.dto.request.EkycUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserVerificationController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private MerchantClient merchantClient;

    @Secured("ROLE_EKYC_USER_UPDATE")
    @PutMapping("/ekyc/update")
    public ResponseEntity<ApiResponse> updateEkycStatus(@RequestBody EkycUpdateRequest updateRequest) {
        ResponseEntity<ApiResponse> apiResponseResponseEntity = adminUserService.updateEkycStatus(updateRequest);
        return apiResponseResponseEntity;
    }

    @Secured("ROLE_EKYC_USER_VIEW_BY_DOC")
    @GetMapping("/ekyc/{userId}/{docType}")
    public ResponseEntity<ApiResponse> getKYCByDocType(@PathVariable(name = "docType") String docType,
                                                       @PathVariable(name = "userId") String userId,
                                                       HttpServletRequest request) {
        try {
            return adminUserService.getUserKYCByDocType(docType, userId);
        } catch (Exception e) {
            ApiResponse response = new Gson().fromJson(e.getMessage(), ApiResponse.class);
            System.out.println(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

    }

    @Secured("ROLE_EKYC_MERCHANT_VIEW_BY_DOC")
    @GetMapping("/merchant/ekyc/{userId}/{docType}")
    public ResponseEntity<ApiResponse> getMerchantKYCByDocType(@PathVariable(name = "docType") String docType,
                                                               @PathVariable(name = "userId") String userId,
                                                               HttpServletRequest request) {
        try {
            return merchantClient.getKYCByDocType(docType, userId);
        } catch (Exception e) {
            ApiResponse response = new Gson().fromJson(e.getMessage(), ApiResponse.class);
            System.out.println(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @Secured("ROLE_EKYC_MERCHANT_UPDATE")
    @PutMapping("/merchant/ekyc/update")
    public ResponseEntity<ApiResponse> j(@RequestBody EkycUpdateRequest updateRequest) {
        ResponseEntity<ApiResponse> apiResponseResponseEntity = merchantClient.updateEkycStatus(updateRequest);
        return apiResponseResponseEntity;
    }

    @Secured("ROLE_EKYC_USER_VIEW_LIST")
    @PostMapping("/ekyc/get/by/key")
    public ResponseEntity<ApiResponse> getUserAllEkycData(@RequestParam(defaultValue = "0") Integer pageNo,
                                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                                          @RequestParam(defaultValue = "createdAt") String sortBy,
                                                          @RequestParam(defaultValue = "false") boolean asc,
                                                          @RequestBody EkycFilterRequest ekycFilterRequest) throws Exception {

        return adminUserService.getEkycByKey(pageNo, pageSize, sortBy, asc, ekycFilterRequest);
    }

    @Secured("ROLE_EKYC_MERCHANT_VIEW_LIST")
    @PostMapping("/merchant/ekyc/get/by/key")
    public ResponseEntity<ApiResponse> getMerchantAllEkycData(@RequestParam(defaultValue = "0") Integer pageNo,
                                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                                              @RequestParam(defaultValue = "createdAt") String sortBy,
                                                              @RequestParam(defaultValue = "false") boolean asc,
                                                              @RequestBody EkycFilterRequest ekycFilterRequest) throws Exception {

        return merchantClient.getEkycByKey(pageNo, pageSize, sortBy, asc, ekycFilterRequest);
    }


}
