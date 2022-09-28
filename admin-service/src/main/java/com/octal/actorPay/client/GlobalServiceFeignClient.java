package com.octal.actorPay.client;

import com.octal.actorPay.constants.EndPointConstants;
import com.octal.actorPay.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static com.octal.actorPay.constants.EndPointConstants.GlobalServiceConstants.GLOBAL_BASE_URL;
import static com.octal.actorPay.constants.EndPointConstants.MerchantServiceConstants.MERCHANT_BASE_URL;

//@FeignClient(name = EndPointConstants.GlobalServiceConstants.GLOBAL_MICROSERVICE, url = GLOBAL_BASE_URL)
//@Service
public interface GlobalServiceFeignClient {

//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.GET_ALL_CATEGORIES_URL, method = RequestMethod.GET)
//    ApiResponse getAllCategoriesPaged(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
//                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
//                                    @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
//                                    @RequestParam(name = "asc", defaultValue = "asc") boolean asc);


//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.GET_ALL_SUB_CATEGORIES_URL, method = RequestMethod.GET)
//    ApiResponse getAllSubCategoriesPaged(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
//                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
//                                      @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
//                                      @RequestParam(name = "asc", defaultValue = "asc") boolean asc);


//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.GET_ALL_ACTIVE_CATEGORIES_URL, method = RequestMethod.GET)
//    ApiResponse getAllActiveCategories();

//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.ADD_NEW_CATEGORIES, method = RequestMethod.POST)
//    ApiResponse createCategory(MultipartFile file, CategoriesDTO categoriesDTO);

//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.ADD_NEW_SUB_CATEGORIES, method = RequestMethod.POST)
//    ApiResponse createSubCategory(MultipartFile file,SubCategoriesDTO subCategoriesDTO);

//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.UPDATE_CATEGORIES, method = RequestMethod.PUT)
//    ApiResponse updateCategory(CategoriesDTO categoriesDTO);

//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.UPDATE_SUB_CATEGORIES, method = RequestMethod.PUT)
//    ApiResponse updateSubCategory(SubCategoriesDTO subCategoriesDTO);


//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.DELETE_CATEGORIES, method = RequestMethod.DELETE)
//    ApiResponse deleteCategoriesByIds(@RequestBody Map<String, List<String>> ids);

//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.DELETE_SUB_CATEGORIES, method = RequestMethod.DELETE)
//    ApiResponse deleteSubCategoriesByIds(@RequestBody Map<String, List<String>> ids);
//
//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.CHANGE_CATEGORY_STATUS, method = RequestMethod.PUT)
//    ApiResponse changeCategoryStatus(@RequestParam(name = "id") String id, @RequestParam(name = "status") boolean status);
//
//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.CHANGE_SUB_CATEGORY_STATUS, method = RequestMethod.PUT)
//    ApiResponse changeSubCategoryStatus(@RequestParam(name = "id") String id, @RequestParam(name = "status") boolean status);

//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.GET_ALL_GLOBAL_SETTINGS, method = RequestMethod.GET)
//    ApiResponse getAllGlobalSettings();

//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.ADD_UPDATE_GLOBAL_SETTINGS, method = RequestMethod.POST)
//    ApiResponse createAndUpdateGlobalSettings(GlobalSettingsDTO globalSettingsDTO);

//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.GET_ALL_CATEGORIES_LIST_URL, method = RequestMethod.GET)
//    ApiResponse getAllCategoriesList();

    // Money_Transfer_Limit
//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.GET_MONEY_TRANSFER_LIMIT_DATA_URL, method = RequestMethod.GET)
//    ApiResponse getMoneyTransferSettingData();

//    @RequestMapping(value = EndPointConstants.GlobalServiceConstants.CREATE_UPDATE_MONEY_TRANSFER_LIMIT_DATA_URL, method = RequestMethod.POST)
//    ApiResponse createAndUpdateMoneyTransferData(MoneyTransferLimitDTO moneyTransferLimitDTO);
}

