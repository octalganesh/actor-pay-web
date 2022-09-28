package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.Categories;
import com.octal.actorPay.entities.Product;
import com.octal.actorPay.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, String> {
    List<Categories> findAllByIsActiveIsTrue();

    Page<Categories> findCategoryByNameContainingIgnoreCaseAndIsActive(String filterByName, Boolean filterByIsActive, Pageable pageRequest);

    Page<Categories> findCategoryByIsActive(Boolean filterByIsActive, Pageable pageRequest);

    Page<Categories> findCategoryByNameContainingIgnoreCase(String filterByName, Pageable pageRequest);

    List<Categories> findCategoryByIsActive(Boolean isActive,Sort sort);

    @Query("select cat from Categories cat where cat.deleted=false")
    Page<Categories> findAllCategories(Pageable pageable);

    Categories findCategoryByName(String categoryName);

    @Modifying
    @Query("delete from Categories  where id in (:catIds)")
    void deleteCategory(@Param("catIds") List<String> catIds);

//    Categories findById(String catId);

    @Query("select c.id from Categories c where c.id in (:ids)")
    List<String> findByIdIn(@Param("ids") List<String> catIds);

}