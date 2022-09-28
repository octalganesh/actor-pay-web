package com.octal.actorPay.client;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.request.ContactUsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@FeignClient(name = "admin-service", url = "http://localhost:8083/")
@Service
public interface AdminFeignClient {

    @RequestMapping(value = "/get/all/active/categories", method = RequestMethod.GET, headers = {"username=admin@yopmail.com"})
    ApiResponse getAllActiveCategories();

//    @RequestMapping(value = "/get/all/subcategories/by/category", method = RequestMethod.GET)
//    ApiResponse getAllActiveSubCategories(@RequestParam("categoryId") String categoryId);

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

    @GetMapping(path = "/taxes/active", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getAllActiveTaxes(@RequestParam(name = "pageNo", required = false) Integer pageNo,
                                                         @RequestParam(name = "pageSize", required = false) Integer pageSize,
                                                         @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                         @RequestParam(name = "asc", defaultValue = "false") boolean asc);

    @GetMapping(path = "/taxes/hsncode/{hsnCode}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getProductByHsnCode(@PathVariable("hsnCode") String hsnCode);

    @GetMapping(path = "/taxes/{taxId}", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getTaxById(@PathVariable("taxId") String taxId);

    @RequestMapping(value = "/get/category/by/name", method = RequestMethod.GET, headers = {"username=admin@yopmail.com"})
    ApiResponse getCategoryByName(@RequestParam("name") String name);

    @RequestMapping(value = "/get/subcategory/by/name", method = RequestMethod.GET, headers = {"username=admin@yopmail.com"})
    ApiResponse getSubCategoryByName(@RequestParam("name") String name);

    @GetMapping(path = "/get/category/by/id", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getCategoryById(@RequestParam("id") String id);

    @GetMapping(path = "/get/subcategory/by/id", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getSubCategoryById(@RequestParam("id") String id);

    @GetMapping(value = "/get/all/categories", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getAllCategories(
            @RequestParam(name = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(name = "asc", required = false, defaultValue = "false") Boolean asc,
            @RequestParam(name = "filterByIsActive", defaultValue = "true", required = false) Boolean filterByIsActive);

    @GetMapping(value = "/get/all/subcategories/by/category", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getAllActiveSubcategoryByCategoryId(
            @RequestParam(name = "categoryId") String categoryId,
            @RequestParam(name = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(name = "asc", defaultValue = "false", required = false) Boolean asc,
            @RequestParam(name = "filterByIsActive", defaultValue = "true", required = false) Boolean filterByIsActive);


    @GetMapping(path = "/taxes/get/all", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getAllTaxes(@RequestParam(name = "pageNo",required = false) Integer pageNo,
                                                   @RequestParam(name = "pageSize",required = false) Integer pageSize,
                                                   @RequestParam(name = "sortBy",defaultValue = "hsnCode", required = false) String sortBy,
                                                   @RequestParam(name = "asc",defaultValue = "false", required = false) boolean asc,
                                                   @RequestParam(name = "isActive", required = false, defaultValue = "true")
                                                           Boolean isActive);

    @GetMapping(value = "/v1/system/configuration/read/by/key/{key}", headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getConfigurationByKey(@PathVariable(name = "key") String key);

    @GetMapping(value = "/v1/system/configuration/read/by/merchant/settings", headers = {"username=admin@yopmail.com"})
    ResponseEntity<ApiResponse> getMerchantSettingsConfig(@RequestParam("keys") List<String> keys);

    @GetMapping(path = "/v1/utility/bill/link", headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> getUtilityBillLink();

    @PostMapping(value = "/admin/contact-us",headers = {"username=admin@yopmail.com"})
    public ResponseEntity<ApiResponse> saveContactUs(@RequestBody ContactUsDTO contactUsDTO);
}