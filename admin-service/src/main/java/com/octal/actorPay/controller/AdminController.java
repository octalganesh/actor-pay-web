package com.octal.actorPay.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.client.AdminUserService;
import com.octal.actorPay.client.CMSFeignClient;
import com.octal.actorPay.client.MerchantClient;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.ContactUsDTO;
import com.octal.actorPay.dto.request.MerchantFilterRequest;
import com.octal.actorPay.dto.request.SubmerchantFilterRequest;
import com.octal.actorPay.dto.request.SystemConfigRequest;
import com.octal.actorPay.entities.ContactUs;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ExceptionUtils;
import com.octal.actorPay.service.AdminService;
import com.octal.actorPay.service.ModulesService;
import com.octal.actorPay.service.SubAdminService;
import com.octal.actorPay.utils.CommonUtils;
import com.octal.actorPay.utils.ResponseUtils;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class AdminController extends PagedItemsController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private SubAdminService subAdminService;

    @Autowired
    private MerchantClient merchantClient;

    @Autowired
    private ModulesService modulesService;

    @Autowired
    private AdminUserService adminUserService;

//    @Autowired
//    private GlobalServiceFeignClient globalServiceFeignClient;

    @Autowired
    private CMSFeignClient cmsFeignClient;

    @GetMapping(value = "/auth/details/by/email/{email}")
    public ResponseEntity getUserByUserName(@PathVariable("email") String email) {
        return ok(adminService.fetchAuthenticatedUserDetailsByEmail(email));
    }

    @GetMapping(value = "/admin/by/email/{email}")
    public ResponseEntity<ApiResponse> getUserByEmailId(@PathVariable(name = "email") String email) {
        User user = adminService.getUserByEmailId(email);
        if (user != null) {
            return new ResponseEntity<ApiResponse>(new
                    ApiResponse("User Details ", user, HttpStatus.OK.name(), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<ApiResponse>(new
                    ApiResponse("User Details not found ", user, HttpStatus.NOT_FOUND.name(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/merchant/by/id/{id}")
    public ResponseEntity<ApiResponse> getMerchantById(@PathVariable(name = "id") String id) {
        return merchantClient.getMerchantById(id);
    }

    @PostMapping(value = "/submerchant/get/all/paged")
    public ResponseEntity getAllSubMerchantUsersPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                                      @RequestParam(defaultValue = "createdAt") String sortBy,
                                                      @RequestParam(defaultValue = "asc") boolean asc,
                                                      @RequestBody SubmerchantFilterRequest submerchantFilterRequest) throws Exception {

        return new ResponseEntity<>(merchantClient.getAllSubMerchantsPaged(pageNo, pageSize, sortBy, asc,submerchantFilterRequest), HttpStatus.OK);
    }

    @PostMapping(value = "merchants/get/all/paged")
    public ResponseEntity<ApiResponse> getAllMerchantUsersPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                                @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                @RequestParam(defaultValue = "false") boolean asc,
                                                                @RequestBody MerchantFilterRequest filterRequest)  throws Exception{

        ResponseEntity<ApiResponse> responseEntity = merchantClient
                .getAllMerchantsPaged(pageNo, pageSize, sortBy, asc,CommonConstant.USER_TYPE_ADMIN, filterRequest);
        return responseEntity;
    }

    @GetMapping(value = "/merchant/get/all")
    public ResponseEntity<ApiResponse> getAllMerchant(@RequestParam(name = "sortBy", defaultValue = "businessName") String sortBy,
                                                      @RequestParam(name = "asc", defaultValue = "false") boolean asc,
                                                      @RequestParam(name = "filterByIsActive", defaultValue = "true", required = false)
                                                          Boolean filterByIsActive
                                                      ) throws Exception {

        ResponseEntity<ApiResponse> responseEntity = merchantClient.getAllMerchant(sortBy, asc,filterByIsActive);
        try {
            if (responseEntity.getBody().getHttpStatus().is2xxSuccessful()) {
                ApiResponse apiResponse = responseEntity.getBody();
                return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse("Merchant details not found", "",
                        HttpStatus.NOT_FOUND.name(), HttpStatus.NOT_FOUND),
                        HttpStatus.NOT_FOUND);
            }
        } catch (FeignException fe) {
            ExceptionUtils.parseFeignExceptionMessage(fe);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error: ", "",
                    HttpStatus.INTERNAL_SERVER_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    @PutMapping("/merchant/update")
    public ResponseEntity<?> updateUser(@RequestBody MerchantDTO merchantDTO) {
        try {
            return merchantClient.updateUser(merchantDTO, CommonConstant.USER_TYPE_ADMIN);
        } catch (FeignException fe) {
           ApiResponse apiResponse = ExceptionUtils.parseFeignExceptionMessage(fe);
            return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error: ", "",
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/merchant/update/settings")
    public ResponseEntity<?> updateMerchantSettingsOnGlobalSettingsUpdate(
            @RequestBody SystemConfigRequest systemConfigRequest,HttpServletRequest request) {
        try {
           String currentLoggedUser = request.getHeader("userName");
           ApiResponse apiResponse = adminService.updateMerchantSettingsOnGlobalSettingsUpdate(systemConfigRequest,currentLoggedUser);
            return  new ResponseEntity<>(apiResponse,apiResponse.getHttpStatus());
        } catch (FeignException fe) {
            ApiResponse apiResponse = ExceptionUtils.parseFeignExceptionMessage(fe);
            return new ResponseEntity<>(apiResponse,HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error: " + e.getMessage(), "",
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST),
                    HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping(value = "/admin/by/id/{id}")
    public ResponseEntity getUserById(@PathVariable("id") String id, final HttpServletRequest request) {
        AdminDTO user = adminService.getUserById(id, request.getHeader("userName"));
        return new ResponseEntity<>(new ApiResponse("Admin data fetched", user,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/user/by/id/{id}")
    public ResponseEntity getUserById(@PathVariable(name = "id") String id) {
        AdminDTO user = adminService.findById(id);
        return new ResponseEntity<>(new ApiResponse("Admin data fetched", user,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PutMapping("/update/admin/profile")
    public ResponseEntity<?> updateUser(@Valid @RequestBody AdminDTO user, final HttpServletRequest request) {
        adminService.updateAdmin(user, request.getHeader("userName"));
        return new ResponseEntity<>(new ApiResponse("Admin updated successfully.", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping(value = "/change/admin/password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordDTO passwordDTO,
                                                      HttpServletRequest request) {
        adminService.changeAdminPassword(passwordDTO, request.getHeader("userName"));
        return new ResponseEntity<>(new ApiResponse("Password successfully changed", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/all/modules")
    public ResponseEntity getModules(@RequestParam(defaultValue = "0") Integer pageNo,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(defaultValue = "name") String sortBy,
                                     @RequestParam(defaultValue = "asc") boolean asc,
                                     HttpServletRequest request) {

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);


        return new ResponseEntity<>(new ApiResponse("All Modules Lists",
                modulesService.getModules(pagedInfo, request.getHeader("userName")),
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @PostMapping("/forget/password")
    public ResponseEntity<?> forgetUserPassword(@RequestBody Map<String, String> data) {
        if (!data.isEmpty()) {
            adminService.resetUserPassword(data.get("emailId"));
            return new ResponseEntity<>(new ApiResponse("Email sent, Please click on the link to reset your password", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Invalid request,email id not found in request", null, String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * once a user received a email with auto generated otp then user has to enter otp with new password
     *
     * @return
     */
    @PostMapping("/reset/password")
    public ResponseEntity<?> resetUserPassword(@Valid @RequestBody ChangePasswordDTO passwordDTO,
                                               HttpServletRequest request) {
        adminService.resetUserPassword(passwordDTO.getToken(), passwordDTO.getNewPassword(), passwordDTO.getConfirmPassword());
        return new ResponseEntity<>(new ApiResponse("Password reset successfully", null, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

//    @GetMapping(value = "/get/all/categories/paged")
//    public ResponseEntity getAllCatgoriesPaged(@RequestParam(defaultValue = "0") Integer pageNo,
//                                               @RequestParam(defaultValue = "10") Integer pageSize,
//                                               @RequestParam(defaultValue = "createdAt") String sortBy,
//                                               @RequestParam(defaultValue = "asc") boolean asc) {
//        return new ResponseEntity<>(merchantClient.getAllCategoriesPaged(pageNo, pageSize, sortBy, asc), HttpStatus.OK);
//    }
//
//    @GetMapping(value = "/get/all/subcategories/paged")
//    public ResponseEntity getAllSubCatgoriesPaged(@RequestParam(defaultValue = "0") Integer pageNo,
//                                                  @RequestParam(defaultValue = "10") Integer pageSize,
//                                                  @RequestParam(defaultValue = "createdAt") String sortBy,
//                                                  @RequestParam(defaultValue = "asc") boolean asc) {
//        return new ResponseEntity<>(merchantClient.getAllSubCategoriesPaged(pageNo, pageSize, sortBy, asc), HttpStatus.OK);
//    }
//
//
//    @GetMapping(value = "/get/all/active/categories")
//    public ResponseEntity getAllActiveCategories() {
//        return new ResponseEntity<>(merchantClient.getAllActiveCategories(), HttpStatus.OK);
//    }
//
//
//    @PostMapping("/add/new/category")
//    public ResponseEntity<ApiResponse> createCategory(@FormDataParam("file") MultipartFile file, @FormDataParam("Metadata") CategoriesDTO categoriesDTO) {
//        ;
//        return new ResponseEntity<>(merchantClient.createCategory(file,categoriesDTO), HttpStatus.OK);
//    }
//
//    @PostMapping("/add/new/subcategory")
//    public ResponseEntity<ApiResponse> createSubCategory(@FormDataParam("file") MultipartFile file,
//			@FormDataParam("Metadata") SubCategoriesDTO subCategories) {
//        return new ResponseEntity<>(merchantClient.createSubCategory(file,subCategories), HttpStatus.OK);
//    }
//
//    @PutMapping("/update/category")
//    public ResponseEntity<ApiResponse> updateCategory(@RequestBody @Valid CategoriesDTO categoriesDTO) {
//
//        return new ResponseEntity<>(merchantClient.updateCategory(categoriesDTO), HttpStatus.OK);
//    }
//
//    @PutMapping("/update/subcategory")
//    public ResponseEntity<ApiResponse> updateSubCategory(@RequestBody @Valid SubCategoriesDTO subCategoriesDTO) {
//
//        return new ResponseEntity<>(merchantClient.updateSubCategory(subCategoriesDTO), HttpStatus.OK);
//    }
//
//    @DeleteMapping("/delete/subcategories/by/ids")
//    public ResponseEntity<ApiResponse> deleteSubCategoryById(@RequestBody Map<String, List<String>> data,
//                                                             HttpServletRequest request) throws InterruptedException {
//
//        return new ResponseEntity<>(merchantClient.deleteSubCategoriesByIds(data), HttpStatus.OK);
//    }
//
//    @DeleteMapping("/delete/categories/by/ids")
//    public ResponseEntity<ApiResponse> deleteCategoryById(@RequestBody Map<String, List<String>> data,
//                                                          HttpServletRequest request) throws InterruptedException {
//
//        return new ResponseEntity<>(merchantClient.deleteCategoriesByIds(data), HttpStatus.OK);
//    }
//
//    @PutMapping("/change/category/status")
//    public ResponseEntity<ApiResponse> changeCategoryStatus(@RequestParam(name = "id") String id, @RequestParam(name = "status") boolean status) throws InterruptedException {
//
//        return new ResponseEntity<>(merchantClient.changeCategoryStatus(id, status), HttpStatus.OK);
//    }
//
//    @PutMapping("/change/subcategory/status")
//    public ResponseEntity<ApiResponse> changeSubCategoryStatus(@RequestParam(name = "id") String id, @RequestParam(name = "status") boolean status) throws InterruptedException {
//
//        return new ResponseEntity<>(merchantClient.changeSubCategoryStatus(id, status), HttpStatus.OK);
//    }
//    @GetMapping(value = "/get/all/categories/paged")
//    public ResponseEntity getAllCatgoriesPaged(@RequestParam(defaultValue = "0") Integer pageNo,
//                                               @RequestParam(defaultValue = "10") Integer pageSize,
//                                               @RequestParam(defaultValue = "createdAt") String sortBy,
//                                               @RequestParam(defaultValue = "asc") boolean asc) {
//        return new ResponseEntity<>(merchantClient.getAllCategoriesPaged(pageNo, pageSize, sortBy, asc), HttpStatus.OK);
//    }
//
//    @GetMapping(value = "/get/all/subcategories/paged")
//    public ResponseEntity getAllSubCatgoriesPaged(@RequestParam(defaultValue = "0") Integer pageNo,
//                                                  @RequestParam(defaultValue = "10") Integer pageSize,
//                                                  @RequestParam(defaultValue = "createdAt") String sortBy,
//                                                  @RequestParam(defaultValue = "asc") boolean asc) {
//        return new ResponseEntity<>(merchantClient.getAllSubCategoriesPaged(pageNo, pageSize, sortBy, asc), HttpStatus.OK);
//    }


//    @GetMapping(value = "/get/all/active/categories")
//    public ResponseEntity getAllActiveCategories() {
//        return new ResponseEntity<>(merchantClient.getAllActiveCategories(), HttpStatus.OK);
//    }
//
//
//    @PostMapping(value = "/add/new/category", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
//    public ResponseEntity<ApiResponse> createCategory(@RequestPart("file") MultipartFile file, @RequestPart("Metadata") CategoriesDTO categoriesDTO) {
//        ;
//        return new ResponseEntity<>(merchantClient.createCategory(file,categoriesDTO), HttpStatus.OK);
//    }
//
//    @PostMapping("/add/new/subcategory")
//    public ResponseEntity<ApiResponse> createSubCategory(@FormDataParam("file") MultipartFile file,
//			@FormDataParam("Metadata") SubCategoriesDTO subCategories) {
//        return new ResponseEntity<>(merchantClient.createSubCategory(file,subCategories), HttpStatus.OK);
//    }

//    @PutMapping("/update/category")
//    public ResponseEntity<ApiResponse> updateCategory(@RequestBody @Valid CategoriesDTO categoriesDTO) {
//
//        return new ResponseEntity<>(merchantClient.updateCategory(categoriesDTO), HttpStatus.OK);
//    }
//
//    @PutMapping("/update/subcategory")
//    public ResponseEntity<ApiResponse> updateSubCategory(@RequestBody @Valid SubCategoriesDTO subCategoriesDTO) {
//
//        return new ResponseEntity<>(merchantClient.updateSubCategory(subCategoriesDTO), HttpStatus.OK);
//    }

    /**
     * to get all active and inactive
     *
     * @return
     */
//    @GetMapping(value = "/get/all/categories")
//    public ResponseEntity getAllActiveInactiveCategories() {
//        return new ResponseEntity<>(merchantClient.getAllCategoriesList(), HttpStatus.OK);
//    }
//
    @GetMapping(value = "/faq/by/id")
    public ResponseEntity getAllActiveInactiveCategories(@RequestParam("id") String id) {
        return new ResponseEntity<>(cmsFeignClient.getFAQDataByID(id), HttpStatus.OK);
    }

    //
//    @GetMapping(value = "/get/global/settings")
//    public ResponseEntity getAllGlobalSettings() {
//        return new ResponseEntity<>(globalServiceFeignClient.getAllGlobalSettings(), HttpStatus.OK);
//    }


    @GetMapping(value = "/get/all/faq/paged")
    public ResponseEntity getAllFAQData(@RequestParam(defaultValue = "0") Integer pageNo,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(defaultValue = "updatedAt") String sortBy,
                                        @RequestParam(defaultValue = "false") boolean asc,
                                        @RequestParam(name = "question", defaultValue = "") String question) {

        return new ResponseEntity<>(cmsFeignClient.getAllFAQData(pageNo, pageSize, sortBy, asc, question), HttpStatus.OK);
    }

    @GetMapping(value = "/get/cms/all/paged")
    public ResponseEntity getAllCMSData(@RequestParam(defaultValue = "0") Integer pageNo,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(defaultValue = "updatedAt") String sortBy,
                                        @RequestParam(defaultValue = "false") boolean asc,
                                        @RequestParam(name = "title", defaultValue = "") String title) {

        return new ResponseEntity<>(cmsFeignClient.getAllCMSData(pageNo, pageSize, sortBy, asc, title), HttpStatus.OK);
    }

    @GetMapping(value = "/read/cms/details/by/id")
    public ResponseEntity getLoyalPointsSettingData(@RequestParam(name = "id") String cmsId) {
        return new ResponseEntity<>(cmsFeignClient.readCmsById(cmsId), HttpStatus.OK);
    }


    @GetMapping(value = "/get/loyal/points/setting/data")
    public ResponseEntity getLoyalPointsSettingData() {
        return new ResponseEntity<>(cmsFeignClient.getLoyalPointsSettingData(), HttpStatus.OK);
    }

    @GetMapping(value = "/email-template/all/paged")
    public ResponseEntity getAllEmailTemplatesData(@RequestParam(defaultValue = "0") Integer pageNo,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestParam(defaultValue = "updatedAt") String sortBy,
                                                   @RequestParam(defaultValue = "false") boolean asc,
                                                   @RequestParam(name = "title", required = false) String title,
                                                   @RequestParam(name = "emailSubject", required = false) String emailSubject,
                                                   @RequestParam(name = "isActive", required = false) Boolean isActive,
                                                   HttpServletRequest request) {

        return new ResponseEntity<>(cmsFeignClient.getAllEmailTemplates(pageNo, pageSize, sortBy, asc, title, emailSubject, isActive), HttpStatus.OK);
    }


//    @PostMapping("/upsert/global/setting")
//    public ResponseEntity<ApiResponse> createAndUpdateGlobalSetting(@RequestBody @Valid GlobalSettingsDTO globalSettingsDTO) {
//        return new ResponseEntity<>(globalServiceFeignClient.createAndUpdateGlobalSettings(globalSettingsDTO), HttpStatus.OK);
//    }

    @PostMapping("/upsert/loyal/reward/points/data")
    public ResponseEntity<ApiResponse> createAndUpdateLoyalRewardPointsData(@RequestBody @Valid LRPointsDTO lrPointsDTO) {
        return new ResponseEntity<>(cmsFeignClient.createAndUpdateLoyalRewardPointsData(lrPointsDTO), HttpStatus.OK);
    }


    @PostMapping("/faq/add/new")
    public ResponseEntity<ApiResponse> createFaq(@RequestBody @Valid FaqDTO faqDTO) throws Exception {
        try {
            return new ResponseEntity<>(cmsFeignClient.addNewFAQ(faqDTO), HttpStatus.OK);
        } catch (
                FeignException feignException) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }

    }

    @PutMapping("/faq/update")
    public ResponseEntity<ApiResponse> updateFaq(@RequestBody @Valid FaqDTO faqDTO) throws Exception {
        try {
            return new ResponseEntity<>(cmsFeignClient.updateFAQ(faqDTO), HttpStatus.OK);
        } catch (FeignException feignException) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    // We can use in future if needed
//    @PostMapping("/cms/add")
//    public ResponseEntity<?> addNewCmsData(@RequestBody @Valid CmsDTO cmsDTO) {
//        return new ResponseEntity<>(cmsFeignClient.addNewCmsData(cmsDTO), HttpStatus.OK);
//    }

//    private ApiResponse catchFeignClientError(FeignException fe) throws Exception {
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//        String content = feignException.contentUTF8();
//        String[] apiResponse = objectMapper.readValue(
//                content, String[].class);
//        System.out.println(Arrays.asList(apiResponse));
//        System.out.println(feignException.getMessage());
//       return Arrays.asList(apiResponse);

//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
//        String content = fe.contentUTF8();
//        Collection<ApiResponse> apiResponse = objectMapper.readValue(
//                content, new TypeReference<Collection<ApiResponse>>() {
//                });
//        System.out.println(apiResponse.stream().findFirst().get());
//        System.out.println(fe.getMessage());
//        return apiResponse.stream().findFirst().get();
//    }

    @PutMapping("/cms/update")
    public ResponseEntity<ApiResponse> updateCMS(@RequestBody @Valid CmsDTO cmsDTO) throws Exception {
        try {
            return new ResponseEntity<>(cmsFeignClient.updateCMS(cmsDTO), HttpStatus.OK);
        } catch (
                FeignException feignException) {
//            List<String> errorResponse = catchFeignClientError(feignException);
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(feignException), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/email-template/delete/by/id")
    public ResponseEntity<?> deleteEmailTemplate(@RequestParam(name = "id") String id) throws Exception {
        try {
            return new ResponseEntity<>(cmsFeignClient.deleteEmailTemplate(id), HttpStatus.OK);
        } catch (FeignException fe) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(fe), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/faq/delete/by/ids")
    public ResponseEntity<ApiResponse> deleteFaq(@RequestBody Map<String, List<String>> data,
                                                 HttpServletRequest request) throws Exception {
        try {
            return new ResponseEntity<>(cmsFeignClient.deleteFAQByIds(data), HttpStatus.OK);
        } catch (FeignException fe) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(fe), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/faq/delete/by/id")
    public ResponseEntity<ApiResponse> deleteFAQById(@RequestParam(name = "id") String id) throws Exception {
        try {
            return new ResponseEntity<>(cmsFeignClient.deleteFAQById(id), HttpStatus.OK);
        } catch (FeignException fe) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(fe), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/email-template/by/id")
    public ResponseEntity<ApiResponse> getEmailTemplateByID(@RequestParam("id") String id) {
        return new ResponseEntity<>(cmsFeignClient.getEmailTemplateById(id), HttpStatus.OK);
    }

    @GetMapping("/email-template/by/slug")
    public ResponseEntity<ApiResponse> getEmailTemplateBySlug(@RequestParam("slug") String slug) {

        return new ResponseEntity<>(cmsFeignClient.getEmailTemplateBySlug(slug), HttpStatus.OK);
    }

    @PutMapping("/email-template/update")
    public ResponseEntity<ApiResponse> updateEmailTemplate(@RequestBody @Valid EmailTemplateDTO emailTemplateDTO)
            throws Exception {
        try {
            return new ResponseEntity<>(cmsFeignClient.emailTempUpdate(emailTemplateDTO), HttpStatus.OK);
        } catch (FeignException fe) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(fe), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/email-template/add")
    public ResponseEntity<ApiResponse> addEmailTemplate(@RequestBody @Valid EmailTemplateDTO emailTemplateDTO) throws Exception {
        try {
            return new ResponseEntity<>(cmsFeignClient.addEmailTemplates(emailTemplateDTO), HttpStatus.OK);
        } catch (FeignException fe) {
            return new ResponseEntity<>(ExceptionUtils.parseFeignExceptionMessage(fe), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("Unknown Error ", null,
                    String.valueOf(HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/get/user/{userIdentity}")
    public ResponseEntity<ApiResponse> getUserIdentity(@PathVariable("userIdentity") String userIdentity) {
        try {
            CommonUserDTO commonUserDTO = adminService.getUserIdentity(userIdentity).orElseThrow(()-> new RuntimeException("User not found"));
            return new ResponseEntity<ApiResponse>(new ApiResponse("User Details ",
                    commonUserDTO,String.valueOf(HttpStatus.OK.value()),HttpStatus.OK),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<ApiResponse>(new ApiResponse(e.getMessage(),
                    null,String.valueOf(101),HttpStatus.OK),HttpStatus.OK);
        }
    }

    @PostMapping("/admin/contact-us")
    public ResponseEntity<ApiResponse> saveContactUs(@RequestBody ContactUsDTO contactUsDTO) throws Exception {
        try {
            adminService.saveContactUs(contactUsDTO);
            return new ResponseEntity<ApiResponse>(new ApiResponse("Thanks for your enquiry",
                    null ,String.valueOf(HttpStatus.OK.value()),HttpStatus.OK),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<ApiResponse>(new ApiResponse(e.getMessage(),
                    null,String.valueOf(101),HttpStatus.OK),HttpStatus.OK);
        }
    }

    @GetMapping("/get/all/admin/contact-us")
    public ResponseEntity getContactUsDetails(@RequestParam(defaultValue = "0") Integer pageNo,
                                              @RequestParam(defaultValue = "10") Integer pageSize,
                                              @RequestParam(defaultValue = "name") String sortBy,
                                              @RequestParam(defaultValue = "asc") boolean asc,
                                              HttpServletRequest request) {
        try{
            final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
            return new ResponseEntity<>(new ApiResponse("Contact Us Data Details",
                    adminService.getContactUsDetails(pageNo,pageSize,pagedInfo, request.getHeader("userName")),String.valueOf(HttpStatus.OK.value()),HttpStatus.OK),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new ApiResponse(e.getMessage(),
                    null,String.valueOf(101),HttpStatus.OK),HttpStatus.OK);
        }
    }

    //  Money_transfer_Limit

//    @GetMapping(value = "/v1/money/transfer/get/money/limit")
//    public ResponseEntity getMoneyTransferSettingData() {
//        return new ResponseEntity<>(globalServiceFeignClient.getMoneyTransferSettingData(), HttpStatus.OK);
//    }
//
//    @PostMapping(value = "/v1/money/transfer/create/or/update")
//    public ResponseEntity<ApiResponse> createAndUpdateLoyalRewardPointsData(@RequestBody @Valid MoneyTransferLimitDTO moneyTransferLimitDTO) {
//        return new ResponseEntity<>(globalServiceFeignClient.createAndUpdateMoneyTransferData(moneyTransferLimitDTO), HttpStatus.OK);
//    }







}