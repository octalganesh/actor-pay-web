package com.octal.actorPay.service;

import com.octal.actorPay.dto.CategoriesDTO;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.SubCategoriesDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface CategoryService {

    void createCategory(CategoriesDTO categoriesDTO);

    void updateCategory(CategoriesDTO categoriesDTO);

    void createSubCategory(SubCategoriesDTO subCategoriesDTO);

    void updateSubCategory(SubCategoriesDTO subCategoriesDTO);

    PageItem<CategoriesDTO> getAllCategoriesPaged(PagedItemInfo pagedInfo, String filterByName,
                                                  Boolean filterByIsActive);

    PageItem<SubCategoriesDTO> getAllSubCategoriesPaged(PagedItemInfo pagedInfo,String filterByName,Boolean filterByIsActive,String categoryName);

    List<CategoriesDTO> getAllActiveCategories();

    CategoriesDTO getCategoryById(String id);

    SubCategoriesDTO getSubCategoryById(String id);

    Map<String, List<String>>  deleteCategory(Map<String, List<String>> userIds) throws Exception;

    Map<String, List<String>> deleteSubCategory(Map<String, List<String>> userIds) throws Exception;

    void changeSubCategory(String categoryId, boolean status);

    void changeCategory(String categoryId, boolean status);

    List<CategoriesDTO> getAllCategories(Boolean filterByIsActive,String sortBy,boolean asc);

    List<SubCategoriesDTO> getSubCategoryByCategoryId(String categoryId, Boolean isActive, String sortBy, boolean asc);

    CategoriesDTO getCategoriesByName(String categoryName);

    SubCategoriesDTO getSubCategoriesByName(String subCategoryName);


}