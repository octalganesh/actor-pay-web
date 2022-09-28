package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.Categories;
import com.octal.actorPay.entities.Product;
import com.octal.actorPay.entities.SubCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoriesRepository extends JpaRepository<SubCategories, String>, JpaSpecificationExecutor<SubCategories> {

    Page<SubCategories> findSubCategoryByNameContainingIgnoreCaseAndIsActive(String filterByName, Boolean filterByIsActive, Pageable pageRequest);

    Page<SubCategories> findSubCategoryByIsActive(Boolean filterByIsActive, Pageable pageRequest);

    Page<SubCategories> findSubCategoryByNameContainingIgnoreCase(String filterByName, Pageable pageRequest);

    List<SubCategories> findSubCategoryByCategoriesIdAndIsActive( String categoryId,Boolean isActive, Sort sort);

    SubCategories findSubCategoryByName(String subCategoryName);

//    SubCategories findById(String catId);

    @Modifying
    @Query("delete from SubCategories  where id in (:catIds)")
    void deleteSubCategory(@Param("catIds") List<String> catIds);

    @Query("select c.id from SubCategories c where c.id in (:ids)")
    List<String> findByIdIn(@Param("ids") List<String> subCatIds);

    @Query("select c.id from SubCategories c where c.categories.id in (:catIds)")
    List<String> findBySubCategoriesById(@Param("catIds") List<String> catIds);

}