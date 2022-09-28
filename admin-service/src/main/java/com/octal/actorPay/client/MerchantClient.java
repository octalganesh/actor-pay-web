package com.octal.actorPay.client;

import com.octal.actorPay.constants.EndPointConstants;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.*;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static com.octal.actorPay.constants.EndPointConstants.MerchantServiceConstants.MERCHANT_BASE_URL;

@FeignClient(name = EndPointConstants.MerchantServiceConstants.MERCHANT_MICROSERVICE,
        url = MERCHANT_BASE_URL,
        configuration = FeignSupportConfig.class)
@Service
public interface MerchantClient {

    @PostMapping(value = "/submerchant/get/all/paged")
    ResponseEntity getAllSubMerchantsPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "10") Integer pageSize, @RequestParam(defaultValue = "createdAt") String sortBy,
                                           @RequestParam(defaultValue = "asc") boolean asc,
                                           @RequestBody SubmerchantFilterRequest submerchantFilterRequest);

    //@RequestMapping(value = EndPointConstants.MerchantServiceConstants.GET_ALL_MERCHANT_URL, method = RequestMethod.GET)
    @PostMapping(value = "merchants/get/all/paged")
    ResponseEntity<ApiResponse> getAllMerchantsPaged(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                     @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                     @RequestParam(defaultValue = "merchant", required = false) String userType,
                                                     @RequestBody MerchantFilterRequest filterRequest);

    @GetMapping(value = "/merchant/get/all")
    ResponseEntity<ApiResponse> getAllMerchant(@RequestParam(name = "sortBy", defaultValue = "businessName") String sortBy,
                                               @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                               @RequestParam(name = "filterByIsActive", defaultValue = "true", required = false)
                                               Boolean filterByIsActive);

    @GetMapping(value = "/merchant/by/id/{id}")
    ResponseEntity<ApiResponse> getMerchantById(@PathVariable("id") String id);

    @PutMapping("/merchant/update")
    ResponseEntity<?> updateUser(@RequestBody MerchantDTO merchantDTO, @RequestParam(value = "userType", required = false) String userType);

    @PutMapping("/merchant/update/settings")
    public ResponseEntity<ApiResponse> updateMerchantSettingsOnGlobalSettingsUpdate(@RequestBody SystemConfigRequest systemConfigRequest);

    @RequestMapping(value = EndPointConstants.MerchantServiceConstants.GET_ALL_CATEGORIES_URL, method = RequestMethod.GET)
    ApiResponse getAllCategoriesPaged(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                      @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                      @RequestParam(name = "asc", defaultValue = "false") boolean asc);


    @RequestMapping(value = EndPointConstants.MerchantServiceConstants.GET_ALL_SUB_CATEGORIES_URL, method = RequestMethod.GET)
    ApiResponse getAllSubCategoriesPaged(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                         @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                         @RequestParam(name = "asc", defaultValue = "false") boolean asc);


    @RequestMapping(value = EndPointConstants.MerchantServiceConstants.GET_ALL_ACTIVE_CATEGORIES_URL, method = RequestMethod.GET)
    ApiResponse getAllActiveCategories();

    @RequestMapping(value = EndPointConstants.MerchantServiceConstants.ADD_NEW_CATEGORIES, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, method = RequestMethod.POST)
    @Headers("Content-Type: multipart/form-data")
    ApiResponse createCategory(@RequestPart("file") MultipartFile file, @RequestPart("Metadata") CategoriesDTO categoriesDTO);

    @RequestMapping(value = EndPointConstants.MerchantServiceConstants.ADD_NEW_SUB_CATEGORIES, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, method = RequestMethod.POST)
//    @Headers("Content-Type: multipart/form-data")
    ApiResponse createSubCategory(@RequestPart("file") MultipartFile file,
                                  @RequestPart("Metadata") SubCategoriesDTO subCategories);


    @RequestMapping(value = EndPointConstants.MerchantServiceConstants.UPDATE_CATEGORIES, method = RequestMethod.PUT)
    ApiResponse updateCategory(CategoriesDTO categoriesDTO);

    @RequestMapping(value = EndPointConstants.MerchantServiceConstants.UPDATE_SUB_CATEGORIES, method = RequestMethod.PUT)
    ApiResponse updateSubCategory(SubCategoriesDTO subCategoriesDTO);


    @RequestMapping(value = EndPointConstants.MerchantServiceConstants.DELETE_CATEGORIES, method = RequestMethod.DELETE)
    ApiResponse deleteCategoriesByIds(@RequestBody Map<String, List<String>> ids);

    @RequestMapping(value = EndPointConstants.MerchantServiceConstants.DELETE_SUB_CATEGORIES, method = RequestMethod.DELETE)
    ApiResponse deleteSubCategoriesByIds(@RequestBody Map<String, List<String>> ids);

    @RequestMapping(value = EndPointConstants.MerchantServiceConstants.CHANGE_CATEGORY_STATUS, method = RequestMethod.PUT)
    ApiResponse changeCategoryStatus(@RequestParam(name = "id") String id, @RequestParam(name = "status") boolean status);

    @RequestMapping(value = EndPointConstants.MerchantServiceConstants.CHANGE_SUB_CATEGORY_STATUS, method = RequestMethod.PUT)
    ApiResponse changeSubCategoryStatus(@RequestParam(name = "id") String id, @RequestParam(name = "status") boolean status);

//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.GET_ALL_GLOBAL_SETTINGS, method = RequestMethod.GET)
//    ApiResponse getAllGlobalSettings();
//
//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.ADD_UPDATE_GLOBAL_SETTINGS, method = RequestMethod.POST)
//    ApiResponse createAndUpdateGlobalSettings(GlobalSettingsDTO globalSettingsDTO);

    @RequestMapping(value = EndPointConstants.MerchantServiceConstants.GET_ALL_CATEGORIES_LIST_URL, method = RequestMethod.GET)
    ApiResponse getAllCategoriesList();

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable("productId") String productId);


    @GetMapping("/products/active/{status}")
    ResponseEntity<ApiResponse> getAllProductByStatus(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                      @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                      @PathVariable("status") Boolean status);

    @PutMapping("/products/active/{status}")
    ResponseEntity<ApiResponse> updateProductStatus(@RequestParam("productId") List<String> productIds,
                                                    @PathVariable("status") Boolean status);

    @PutMapping("/products/change/status")
    ResponseEntity<ApiResponse> changeProductStatus(@RequestParam(name = "id") String id, @RequestParam(name = "status") Boolean status);

    @PostMapping("/products/list/paged")
    public ResponseEntity<ApiResponse> getAllProduct(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                     @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                     @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                     @RequestBody ProductFilterRequest filterRequest);

    @GetMapping("/products/subcate/associated")
    ResponseEntity<ApiResponse> isSubcategoryAssociated(@RequestParam(name = "subcategoryId") String subcategoryId);

    @GetMapping("/products/cate/associated")
    ResponseEntity<ApiResponse> isCategoryAssociated(@RequestParam(name = "categoryId") String categoryId);

    @PutMapping("/ekyc/update")
    public ResponseEntity<ApiResponse>
    updateEkycStatus(@RequestBody EkycUpdateRequest updateRequest);

    @GetMapping("/ekyc/{userId}/{docType}")
    public ResponseEntity<ApiResponse> getKYCByDocType(@PathVariable(name = "docType") String docType,
                                                       @PathVariable(name = "userId") String userId);

    @PostMapping("/ekyc/get/by/key")
    public ResponseEntity<ApiResponse> getEkycByKey(@RequestParam(defaultValue = "0") Integer pageNo,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestParam(defaultValue = "createdAt") String sortBy,
                                                    @RequestParam(defaultValue = "false") boolean asc,
                                                    @RequestBody EkycFilterRequest ekycFilterRequest);

    @GetMapping("/merchants/getAllMerchantsCount")
    public ResponseEntity<ApiResponse> getAllMerchantsCount();

    @GetMapping("/products/all-products-counts")
    public ResponseEntity<ApiResponse> getAllProductProductCount();

}
