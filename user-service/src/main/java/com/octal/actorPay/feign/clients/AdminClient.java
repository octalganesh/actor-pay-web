package com.octal.actorPay.feign.clients;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.ApplyPromoCodeResponse;
import com.octal.actorPay.dto.LoyaltyRewardHistoryResponse;
import com.octal.actorPay.dto.request.ContactUsDTO;
import com.octal.actorPay.dto.request.CovertRewardsRequest;
import com.octal.actorPay.dto.request.OfferFilterRequest;
import com.octal.actorPay.dto.request.PromoCodeFilter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "admin-service", url = "http://localhost:8083/")
@Service
public interface AdminClient {

    @GetMapping(path = "/email-template/by/id", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getEmailTemplateById(@RequestParam(name = "id") String id);

    @GetMapping(path = "/email-template/by/slug", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getEmailTemplateBySlug(@RequestParam(name = "slug") String slug);

    @PostMapping(path = "/offers/available", headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getAvailableOffers(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                   @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                   @RequestBody OfferFilterRequest filterRequest);

    @GetMapping(path = "/offers/code/{promoCode}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getAllOfferByPromoCode(@PathVariable("promoCode") String promoCode);

    @GetMapping(path = "/v1/loyalty/rewards/userId/{userId}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getRewardPointsByUserId(@RequestParam(defaultValue = "0") Integer pageNo,
                                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                                               @RequestParam(defaultValue = "createdAt") String sortBy,
                                                               @RequestParam(defaultValue = "true") boolean asc,
                                                               @PathVariable("userId") String userId);

    @GetMapping(path = "/v1/loyalty/rewards/total/{userId}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getTotalRewards(@PathVariable("userId") String userId);

    @PostMapping(path = "/v1/loyalty/rewards/convert", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> convertReward(@RequestBody CovertRewardsRequest covertRewardsRequest);

    @PostMapping(path = "/v1/loyalty/rewards/update/history", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> updateReward(@RequestBody LoyaltyRewardHistoryResponse updateRequest);

    @GetMapping(path = "/v1/loyalty/rewards/{event}/{userId}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getRewardByEvent(@PathVariable("event") String event, @PathVariable("userId") String userId);

    @PostMapping(path = "/v1/promoCode/apply", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> applyPromoCode(@RequestBody PromoCodeFilter filterRequest);

    @RequestMapping(value = "/get/all/active/categories", method = RequestMethod.GET, headers = {"username=admin@yopmail.com"})
    ApiResponse getAllActiveCategories();

    @RequestMapping(value = "/get/all/subcategories/by/category", method = RequestMethod.GET, headers = {"username=admin@yopmail.com"})
    ApiResponse getAllActiveSubCategories(@RequestParam("categoryId") String categoryId);

    @RequestMapping(value = "/get/all/categories/paged", method = RequestMethod.GET, headers = {"username=admin@yopmail.com"})
    ApiResponse getAllCategoriesPaged(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                      @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                      @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                      @RequestParam(name = "filterByName", required = false) String filterByName,
                                      @RequestParam(name = "filterByIsActive", required = false) Boolean filterByIsActive);

    @RequestMapping(value = "/get/all/subcategories/paged", method = RequestMethod.GET, headers = {"username=admin@yopmail.com"})
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

    @GetMapping(value = "/v1/system/configuration/read/by/keys", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> readByKeys(@RequestParam("keys") List<String> keys);

    @GetMapping(path = "/v1/utility/bill/link", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getUtilityBillLink();

    @PostMapping(path = "/v1/promoCode/save", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> savePromoCode(@RequestBody List<ApplyPromoCodeResponse> request);
    @GetMapping(path = "/v1/promoCode/getHistory/{orderId}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getPromoCodeHistoryByOrderId(@PathVariable(name = "orderId") String orderId);

    @PostMapping("/admin/contact-us")
    public ResponseEntity<ApiResponse> saveContactUs(@RequestBody ContactUsDTO contactUsDTO);
}


