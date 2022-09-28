package com.octal.actorPay.client;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.LoyaltyRewardHistoryResponse;
import com.octal.actorPay.dto.request.OfferFilterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "admin-service", url = "http://localhost:8083/")
@Service
public interface PaymentAdminClient {

    @GetMapping(value = "/email-template/by/id", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getEmailTemplateById(@RequestParam(name = "id") String id);

    @GetMapping(value = "/email-template/by/slug", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getEmailTemplateBySlug(@RequestParam(name = "slug") String slug);

    @PostMapping(value = "/offers/available", headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getAvailableOffers(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                   @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                   @RequestBody OfferFilterRequest filterRequest);

    @RequestMapping(value = "/get/all/active/categories", headers = {"username=admin@yopmail.com"}, method = RequestMethod.GET)
    ApiResponse getAllActiveCategories();

    @RequestMapping(value = "/get/all/subcategories/by/category", headers = {"username=admin@yopmail.com"}, method = RequestMethod.GET)
    ApiResponse getAllActiveSubCategories(@RequestParam("categoryId") String categoryId);

    @RequestMapping(value = "/get/all/categories/paged", headers = {"username=admin@yopmail.com"}, method = RequestMethod.GET)
    ApiResponse getAllCategoriesPaged(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                      @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                      @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                      @RequestParam(name = "filterByName", required = false) String filterByName,
                                      @RequestParam(name = "filterByIsActive", required = false) Boolean filterByIsActive);

    @RequestMapping(value = "/get/all/subcategories/paged", headers = {"username=admin@yopmail.com"}, method = RequestMethod.GET)
    ApiResponse getAllSubCategoriesPaged(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                         @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                         @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                         @RequestParam(name = "filterByName", required = false) String filterByName,
                                         @RequestParam(name = "filterByIsActive", required = false) Boolean filterByIsActive,
                                         @RequestParam(name = "filterByCategoryName", required = false) String filterByCategoryName);


    @GetMapping(value = "/admin/by/email/{email}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getUserByEmailId(@PathVariable(name = "email") String email);

    @GetMapping(value = "/v1/system/configuration/read/by/key/{key}", headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getConfigurationByKey(@PathVariable(name = "key") String key);

    @GetMapping(value = "/v1/system/configuration/read/by/merchant/settings", headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getMerchantSettingsConfig(@RequestParam("keys") List<String> keys);

    @GetMapping(value = "/v1/system/configuration/read/by/merchant/default/settings", headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getMerchantDefaultSettingsConfig();

    @GetMapping(value = "/user/by/id/{id}", headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getUserById(@PathVariable(name = "id") String id);

    @GetMapping(value = "/get/user/{userIdentity}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getUserIdentity(@PathVariable(name = "userIdentity") String userIdentity);

    @PostMapping(value = "/v1/loyalty/rewards/update/history", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> updateReward(@RequestBody LoyaltyRewardHistoryResponse updateRequest);

    @GetMapping(value = "/v1/loyalty/rewards/{event}/{userId}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getRewardByEvent(@PathVariable("event") String event, @PathVariable("userId") String userId);

}


