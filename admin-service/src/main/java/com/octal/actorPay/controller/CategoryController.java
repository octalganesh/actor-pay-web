package com.octal.actorPay.controller;

import com.octal.actorPay.dto.*;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.service.CategoryService;
import com.octal.actorPay.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CategoryController extends PagedItemsController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UploadService uploadService;

    @Secured("ROLE_CATEGORY_ADD_NEW")
    @PostMapping(value = "/add/new/category", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse> createCategory(
            @Valid @RequestPart(value = "file", required = false) MultipartFile file,
            @Valid @RequestPart("Metadata") CategoriesDTO categoriesDTO)
            throws IllegalStateException, IOException {
    try {
    if (file == null || file.isEmpty()) {
        throw new RuntimeException("Please select Image for Category");
    }
    CategoriesDTO isExist = categoryService.getCategoriesByName(categoriesDTO.getName());
    if (isExist != null) {
        throw new RuntimeException(String.format("Category name is already found - %s ", categoriesDTO.getName()));
    }

    String s3Path = uploadService.uploadFileToS3(file, "Category/" + categoriesDTO.getName());
    categoriesDTO.setImage(s3Path);
    if (categoriesDTO.getStatus() == null)
        categoriesDTO.setStatus(true);
    categoryService.createCategory(categoriesDTO);
    return new ResponseEntity<>(new ApiResponse("Category created successfully", null,
            String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }
    catch (Exception e){
        return new ResponseEntity<>(new ApiResponse("Please select Image for Category", null,
                String.valueOf(101), HttpStatus.OK), HttpStatus.OK);
    }
    }


    @Secured("ROLE_SUB_CATEGORY_ADD_NEW")
    @PostMapping("/add/new/subcategory")
    public ResponseEntity<ApiResponse> createSubCategory(@Valid @RequestPart(value = "file", required = false) MultipartFile file,
                                                         @Valid @RequestPart("Metadata") SubCategoriesDTO subCategories) throws IllegalStateException, IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please select Image for Subcategory");
        }
        SubCategoriesDTO isExist = categoryService.getSubCategoriesByName(subCategories.getName());
        if (isExist != null) {
            throw new RuntimeException(String.format("Subcategory name is already found %s ", subCategories.getName()));
        }
        CategoriesDTO categoriesDTO = categoryService.getCategoryById(subCategories.getCategoryId());
        ;
        if (categoriesDTO == null) {
            throw new ObjectNotFoundException(String.format("The Category is not valid or not found - %s", subCategories.getCategoryId()));
        } else {
            subCategories.setCategoryName(categoriesDTO.getName());
        }
        String s3Path = uploadService.uploadFileToS3(file, "Category/" + subCategories.getCategoryName() + "/" + subCategories.getName());
        subCategories.setImage(s3Path);
        if (!subCategories.isActive())
            subCategories.setActive(true);
        categoryService.createSubCategory(subCategories);

        return new ResponseEntity<>(new ApiResponse("Sub Category created successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_CATEGORY_LIST_VIEW")
    @GetMapping(value = "/get/all/categories/paged")
    public ResponseEntity getAllCategoriesPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                @RequestParam(defaultValue = "createdAt") String sortBy,
                                                @RequestParam(defaultValue = "false") Boolean asc,
                                                @RequestParam(name = "filterByName", required = false) String filterByName,
                                                @RequestParam(name = "filterByIsActive", required = false) Boolean filterByIsActive,
                                                HttpServletRequest request) {

        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<CategoriesDTO> allCategories = categoryService.getAllCategoriesPaged(pagedInfo, filterByName, filterByIsActive);
        return new ResponseEntity<>(new ApiResponse("All Categories Data",
                allCategories, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_SUB_CATEGORY_LIST_VIEW")
    @GetMapping(value = "/get/all/subcategories/paged")
    public ResponseEntity<ApiResponse> getAllSubCategoriesPaged(@RequestParam(defaultValue = "0") Integer pageNo,
                                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                                @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                @RequestParam(defaultValue = "false") boolean asc,
                                                                @RequestParam(name = "filterByName", required = false) String filterByName,
                                                                @RequestParam(name = "filterByIsActive", required = false) Boolean filterByIsActive,
                                                                @RequestParam(name = "filterByCategoryName", required = false) String filterByCategoryName,
                                                                HttpServletRequest request) {
        final PagedItemInfo pagedInfo = createPagedInfo(pageNo, pageSize, sortBy, asc, null);
        PageItem<SubCategoriesDTO> allCategories = categoryService.getAllSubCategoriesPaged(
                pagedInfo, filterByName, filterByIsActive, filterByCategoryName);
        return new ResponseEntity<>(new ApiResponse("All sub categories data",
                allCategories, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping(value = "/get/all/active/categories")
    public ResponseEntity getAllActiveCategories() {
        return new ResponseEntity<>(new ApiResponse("All categories data",
                categoryService.getAllActiveCategories(), String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_CATEGORY_UPDATE")
    @PutMapping("/update/category")
    public ResponseEntity<ApiResponse> updateCategory(@RequestPart(value = "file", required = false) MultipartFile file,
                                                      @RequestPart("Metadata") CategoriesDTO categoriesDTO)
            throws IllegalStateException, IOException {
        CategoriesDTO isExist = categoryService.getCategoriesByName(categoriesDTO.getName());

        if (isExist != null && !(isExist.getId().equalsIgnoreCase(categoriesDTO.getId()))) {
            throw new RuntimeException(String.format("Category name is already found - %s ", categoriesDTO.getName()));
        }
        if ((isExist != null && StringUtils.isEmpty(isExist.getImage())) && (file == null || file.isEmpty())) {
            throw new RuntimeException("Image is mandatory for Category");
        }
        if (file != null) {
            String s3Path = uploadService.uploadFileToS3(file, "Category/" + categoriesDTO.getName());
            categoriesDTO.setImage(s3Path);
        }
        categoryService.updateCategory(categoriesDTO);
        return new ResponseEntity<>(new ApiResponse("Category updated successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_SUB_CATEGORY_UPDATE")
    @PutMapping("/update/subcategory")
    public ResponseEntity<ApiResponse> updateSubCategory(@RequestPart(value = "file", required = false) MultipartFile file,
                                                         @RequestPart("Metadata") SubCategoriesDTO subCategoriesDTO)
            throws IllegalStateException, IOException {
        SubCategoriesDTO isExist = categoryService.getSubCategoriesByName(subCategoriesDTO.getName());
        if (isExist != null && !(isExist.getId().equalsIgnoreCase(subCategoriesDTO.getId()))) {
            throw new RuntimeException(String.format("Subcategory name is already found - %s ", subCategoriesDTO.getName()));
        }
//        System.out.println(StringUtils.isEmpty(isExist.getImage()));
        System.out.println((isExist != null && StringUtils.isEmpty(isExist.getImage())) && (file == null || file.isEmpty()));
        if ((isExist != null && StringUtils.isEmpty(isExist.getImage())) && (file == null || file.isEmpty())) {
            throw new RuntimeException("Image is mandatory for Subcategory");
        }
        CategoriesDTO categoriesDTO = categoryService.getCategoryById(subCategoriesDTO.getCategoryId());
        ;
        if (categoriesDTO == null) {
            throw new ObjectNotFoundException(String.format("The Category is not valid or not found - %s", subCategoriesDTO.getCategoryId()));
        } else {
            subCategoriesDTO.setCategoryName(categoriesDTO.getName());
        }
        if (file != null) {
            String s3Path = uploadService.uploadFileToS3(file, "Category/" + subCategoriesDTO.getCategoryName() + "/" + subCategoriesDTO.getName());
            subCategoriesDTO.setImage(s3Path);
        }
        categoryService.updateSubCategory(subCategoriesDTO);
        return new ResponseEntity<>(new ApiResponse("Sub Category updated successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_CATEGORY_VIEW_BY_ID")
    @GetMapping("/get/category/by/id")
    public ResponseEntity<ApiResponse> getCategoryById(@RequestParam("id") String id) {
        return new ResponseEntity<>(new ApiResponse("Category Details",
                categoryService.getCategoryById(id),
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_SUB_CATEGORY_VIEW_BY_ID")
    @GetMapping("/get/subcategory/by/id")
    public ResponseEntity<ApiResponse> getSubCategoryById(@RequestParam("id") String id) {

        return new ResponseEntity<>(new ApiResponse("Sub Category Details",
                categoryService.getSubCategoryById(id),
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_CATEGORY_DELETE")
    @DeleteMapping("/delete/categories/by/ids")
    public ResponseEntity<ApiResponse> deleteCategoryById(@RequestBody Map<String, List<String>> data,
                                                          HttpServletRequest request) throws Exception {
        Map<String, List<String>> listMap = categoryService.deleteCategory(data);
        List<String> availableCats = listMap.get("availableCats");
        List<String> notAvailableCats = listMap.get("notAvailableCats");
        StringBuilder stringBuilder = new StringBuilder();
        if (!availableCats.isEmpty()) {
            String availableSubCatsMsg = " Category(s) deleted successfully " + availableCats;
            stringBuilder.append(availableSubCatsMsg);
        }
        if (!notAvailableCats.isEmpty()) {
            String notAvailableSubCatsMsg = " Following Category Id(s) are Invalid or already deleted " + notAvailableCats;
            if (stringBuilder.length() > 0)
                stringBuilder.append("\n").append(notAvailableSubCatsMsg);
            else
                stringBuilder.append(notAvailableSubCatsMsg);
        }
        return new ResponseEntity<>(new ApiResponse(stringBuilder.toString()
                , "", String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_SUB_CATEGORY_DELETE")
    @DeleteMapping("/delete/subcategories/by/ids")
    public ResponseEntity<ApiResponse> deleteSubCategoryById(@RequestBody Map<String, List<String>> data,
                                                             HttpServletRequest request) throws Exception {
//        boolean isAssociated = product
        Map<String, List<String>> listMap = categoryService.deleteSubCategory(data);
        List<String> availableSubCats = listMap.get("availableSubCats");
        List<String> notAvailableSubCats = listMap.get("notAvailableSubCats");
//        List<String> deletedSubCats = listMap.get("deletedSubCats");
        StringBuilder stringBuilder = new StringBuilder();
        String delimiter = ",";
        if (!availableSubCats.isEmpty()) {
            String availableSubCatsMsg = " Category(s) deleted successfully " + availableSubCats.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(delimiter));
            ;
            stringBuilder.append(availableSubCatsMsg);
        }
        if (!notAvailableSubCats.isEmpty()) {
            String notAvailableSubCatsMsg = " Following Category Id(s) are Invalid or already deleted " + notAvailableSubCats.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(delimiter));
            if (stringBuilder.length() > 0)
                stringBuilder.append(" \n").append(notAvailableSubCatsMsg);
            else
                stringBuilder.append(notAvailableSubCatsMsg);
        }
        return new ResponseEntity<>(new ApiResponse(stringBuilder.toString()
                , "", String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_CATEGORY_CHANGE_STATUS")
    @PutMapping("/change/category/status")
    public ResponseEntity<ApiResponse> changeCategory(@RequestParam(name = "id") String id, @RequestParam(name = "status") boolean status) throws InterruptedException {
        categoryService.changeCategory(id, status);
        return new ResponseEntity<>(new ApiResponse("Category status updated successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @Secured("ROLE_SUB_CATEGORY_CHANGE_STATUS")
    @PutMapping("/change/subcategory/status")
    public ResponseEntity<ApiResponse> changeSubCategory(@RequestParam(name = "id") String id, @RequestParam(name = "status") boolean status) throws InterruptedException {
        categoryService.changeSubCategory(id, status);
        return new ResponseEntity<>(new ApiResponse("Sub Category status updated successfully", null,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }


    @GetMapping(value = "/get/all/categories")
    public ResponseEntity<ApiResponse> getAllCategories(
            @RequestParam(name = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(name = "asc", required = false, defaultValue = "false") Boolean asc,
            @RequestParam(name = "filterByIsActive", defaultValue = "true", required = false) Boolean filterByIsActive) {
        List<CategoriesDTO> allCategories = categoryService.getAllCategories(filterByIsActive, sortBy, asc);
        return new ResponseEntity<>(new ApiResponse("All Categories Data",
                allCategories, String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }


    @GetMapping(value = "/get/all/subcategories/by/category")
    public ResponseEntity<ApiResponse> getAllActiveSubcategoryByCategoryId(@RequestParam("categoryId") String categoryId,
                                                              @RequestParam(name = "sortBy",
                                                                      defaultValue = "name", required = false)
                                                                      String sortBy,
                                                              @RequestParam(name = "asc", required = false,
                                                                      defaultValue = "false")
                                                                      Boolean asc,
                                                              @RequestParam(name = "filterByIsActive",
                                                                      defaultValue = "true", required = false)
                                                                      Boolean filterByIsActive) {
//        String categoryId,Boolean isActive, String sortBy,boolean asc
        List<SubCategoriesDTO> pageItem = categoryService.getSubCategoryByCategoryId(categoryId,filterByIsActive,sortBy,asc);
        return new ResponseEntity<>(new ApiResponse("Active Sub Category from category id", pageItem,
                String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
    }

    @GetMapping("/get/category/by/name")
    public ResponseEntity<ApiResponse> getCategoryByName(@RequestParam("name") String name) {
        CategoriesDTO categoriesDTO = categoryService.getCategoriesByName(name);
        if (categoriesDTO != null) {
            return new ResponseEntity<>(new ApiResponse("Category Details",
                    categoriesDTO,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("No such a category exist",
                    "",
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }
    }

    @GetMapping("/get/subcategory/by/name")
    public ResponseEntity<ApiResponse> getSubCategoryByName(@RequestParam("name") String name) {
        SubCategoriesDTO subCategoriesDTO = categoryService.getSubCategoriesByName(name);
        if (subCategoriesDTO != null) {
            return new ResponseEntity<>(new ApiResponse("SubCategory Details", subCategoriesDTO,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("No such a SubCategory exist", subCategoriesDTO,
                    String.valueOf(HttpStatus.OK.value()), HttpStatus.OK), HttpStatus.OK);
        }
    }

}

