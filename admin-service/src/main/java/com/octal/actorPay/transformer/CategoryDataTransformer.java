package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.CategoriesDTO;
import com.octal.actorPay.dto.MerchantDTO;
import com.octal.actorPay.dto.SubCategoriesDTO;
import com.octal.actorPay.entities.Categories;
import com.octal.actorPay.entities.SubCategories;

import java.util.function.Function;

public class CategoryDataTransformer {

    public static Function<Categories, CategoriesDTO> CATEGORY_TO_DTO = (category) -> {

        CategoriesDTO dto = new CategoriesDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setStatus(category.getActive());
        dto.setDescription(category.getDescription());
        dto.setImage(category.getImage());
        dto.setCreatedAt(category.getCreatedAt());
        return dto;
    };

    public static Function<SubCategories, SubCategoriesDTO> SUBCATEGORY_TO_DTO = (subCategories) -> {

        SubCategoriesDTO dto = new SubCategoriesDTO();
        dto.setId(subCategories.getId());
        dto.setName(subCategories.getName());
        dto.setActive(subCategories.getActive());
        dto.setDescription(subCategories.getDescription());
        dto.setImage(subCategories.getImage());
        dto.setCategoryId(subCategories.getCategories().getId());
        dto.setCategoryName(subCategories.getCategories().getName());
        dto.setCreatedAt(subCategories.getCreatedAt());
        return dto;
    };

}