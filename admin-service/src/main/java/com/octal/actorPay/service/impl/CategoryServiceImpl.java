package com.octal.actorPay.service.impl;

import com.octal.actorPay.client.MerchantClient;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.dto.request.SubcategoryFilterRequest;
import com.octal.actorPay.entities.Categories;
import com.octal.actorPay.entities.SubCategories;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.repositories.CategoriesRepository;
import com.octal.actorPay.repositories.SubCategoriesRepository;
import com.octal.actorPay.service.CategoryService;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.CategoryDataTransformer;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CategoryServiceImpl implements CategoryService {

    private CategoriesRepository categoriesRepository;

    private SubCategoriesRepository subCategoriesRepository;

    private SpecificationFactory<SubCategories> subCategoriesSpecificationFactory;

    private MerchantClient merchantClient;

    public CategoryServiceImpl(CategoriesRepository categoriesRepository, SubCategoriesRepository subCategoriesRepository,
                               SpecificationFactory<SubCategories> subCategoriesSpecificationFactory, MerchantClient merchantClient) {
        this.categoriesRepository = categoriesRepository;
        this.subCategoriesRepository = subCategoriesRepository;
        this.subCategoriesSpecificationFactory = subCategoriesSpecificationFactory;
        this.merchantClient = merchantClient;

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createCategory(CategoriesDTO categoriesDTO) {
        // TODO add admin validation
        Categories categories = new Categories();
        categories.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        mapCategoriesData(categoriesDTO, categories);
        categoriesRepository.save(categories);
    }

    private void mapCategoriesData(CategoriesDTO categoriesDTO, Categories categories) {
        categories.setName(categoriesDTO.getName());
        categories.setDescription(categoriesDTO.getDescription());
        categories.setActive(categoriesDTO.getStatus());
        categories.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        categories.setImage(categoriesDTO.getImage());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCategory(CategoriesDTO categoriesDTO) {
        // TODO add admin validation
        Optional<Categories> category = categoriesRepository.findById(categoriesDTO.getId());
        if (category.isPresent()) {
            mapCategoriesData(categoriesDTO, category.get());
            categoriesRepository.save(category.get());
        } else {
            throw new ObjectNotFoundException("Category not found for the given id: " + categoriesDTO.getId());
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createSubCategory(SubCategoriesDTO subCategoriesDTO) {
        // TODO add admin validation
        Optional<Categories> category = categoriesRepository.findById(subCategoriesDTO.getCategoryId());
        if (category.isPresent()) {
            SubCategories subCategories = new SubCategories();
            subCategories.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
            mapSubCategoryData(subCategoriesDTO, category.get(), subCategories);
            subCategoriesRepository.save(subCategories);
        } else {
            throw new ObjectNotFoundException("Category not found for the given id: " + subCategoriesDTO.getCategoryId());
        }

    }

    private void mapSubCategoryData(SubCategoriesDTO subCategoriesDTO, Categories category, SubCategories subCategories) {
        subCategories.setName(subCategoriesDTO.getName());
        subCategories.setDescription(subCategoriesDTO.getDescription());
        subCategories.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        subCategories.setActive(subCategoriesDTO.isActive());
        subCategories.setImage(subCategoriesDTO.getImage());
        subCategories.setCategories(category);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSubCategory(SubCategoriesDTO subCategoriesDTO) {
        // TODO add admin validation
        Optional<SubCategories> subCategories = subCategoriesRepository.findById(subCategoriesDTO.getId());
        if (subCategories.isPresent()) {
            Optional<Categories> category = categoriesRepository.findById(subCategoriesDTO.getCategoryId());
            if (category != null) {
                subCategoriesDTO.setCategoryName(category.get().getName());
                mapSubCategoryData(subCategoriesDTO, category.get(), subCategories.get());
                subCategoriesRepository.save(subCategories.get());
            } else {
                throw new ObjectNotFoundException(String.format("Invalid Category is selected %s", subCategoriesDTO.getCategoryId()));
            }
        } else {
            throw new ObjectNotFoundException("Sub Category not found for the given id: " + subCategoriesDTO.getId());
        }
    }

    @Override
    public PageItem<CategoriesDTO> getAllCategoriesPaged(PagedItemInfo pagedInfo, String filterByName,
                                                         Boolean filterByIsActive) {
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Categories.class, pagedInfo);
        GenericSpecificationsBuilder<Categories> builder = new GenericSpecificationsBuilder<>();
        // prepare search query
        Page<Categories> pagedResult = null;
        if (StringUtils.isNotBlank(filterByName) && filterByIsActive != null) {
            pagedResult = categoriesRepository.findCategoryByNameContainingIgnoreCaseAndIsActive(filterByName, filterByIsActive, pageRequest);
        } else if (filterByIsActive != null) {
            pagedResult = categoriesRepository.findCategoryByIsActive(filterByIsActive, pageRequest);
        } else if (StringUtils.isNotBlank(filterByName)) {
            pagedResult = categoriesRepository.findCategoryByNameContainingIgnoreCase(filterByName, pageRequest);
        } else {
            pagedResult = categoriesRepository.findAllCategories(pageRequest);
        }
        List<CategoriesDTO> content = pagedResult.getContent().stream().map(CategoryDataTransformer.CATEGORY_TO_DTO)
                .collect(Collectors.toList());

        return new PageItem<>(pagedResult.getTotalPages(),
                pagedResult.getTotalElements(),
                content, pagedInfo.page,
                pagedInfo.items);
    }


    @Override
    public PageItem<SubCategoriesDTO> getAllSubCategoriesPaged(PagedItemInfo pagedInfo,
                                                               String filterByName, Boolean filterByIsActive, String categoryName) {
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(SubCategories.class, pagedInfo);
        GenericSpecificationsBuilder<SubCategories> builder = new GenericSpecificationsBuilder<>();
        SubcategoryFilterRequest filterRequest = new SubcategoryFilterRequest();
        filterRequest.setFilterByCategoryName(categoryName);
        filterRequest.setFilterByName(filterByName);
        filterRequest.setFilterByIsActive(filterByIsActive);
        // prepare search query
        prepareSubcategoriesSearchQuery(filterRequest, builder);
        Page<SubCategories> pagedResult = subCategoriesRepository.findAll(builder.build(), pageRequest);
        List<SubCategoriesDTO> content = pagedResult.getContent().stream().map(CategoryDataTransformer.SUBCATEGORY_TO_DTO)
                .collect(Collectors.toList());
        return new PageItem<>(pagedResult.getTotalPages(),
                pagedResult.getTotalElements(),
                content, pagedInfo.page,
                pagedInfo.items);
    }

    @Override
    public List<CategoriesDTO> getAllActiveCategories() {
        return categoriesRepository.findAllByIsActiveIsTrue()
                .stream()
                .map(CategoryDataTransformer.CATEGORY_TO_DTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoriesDTO getCategoryById(String id) {
        Optional<Categories> category = categoriesRepository.findById(id);
        if (category.isPresent()) {
            return category.map(CategoryDataTransformer.CATEGORY_TO_DTO).get();
        } else {
            throw new ObjectNotFoundException("Category not found for the given id: " + id);
        }
    }

    @Override
    public SubCategoriesDTO getSubCategoryById(String id) {
        Optional<SubCategories> subCategories = subCategoriesRepository.findById(id);
        if (subCategories.isPresent()) {
            return subCategories.map(CategoryDataTransformer.SUBCATEGORY_TO_DTO).get();
        } else {
            throw new ObjectNotFoundException("Sub Category not found for the given id: " + id);
        }
    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
    @Transactional
    public Map<String, List<String>> deleteCategory(Map<String, List<String>> data) throws Exception {
//        categoriesRepository.deleteInBatch(categoriesRepository.findAllById(data.get("categoryIds")));
        List<String> availableCats = new ArrayList<>();
        List<String> notAvailableCats = new ArrayList<>();
        List<String> deletedCats = new ArrayList<>();
        Map<String, List<String>> listMap = new HashMap<>();
        List<String> catIds = data.get("categoryIds");
        List<String> catsWithSubCat = subCategoriesRepository.findBySubCategoriesById(catIds);
        if (!catsWithSubCat.isEmpty()) {
            throw new RuntimeException("Categories are associated with Subcategory Id(s) - Please delete SubCategories first " + catsWithSubCat);
        }
//        categoriesRepository.deleteCategory(catIds);
        List<String> associatedCates = new ArrayList<>();
        for (String categoryId : catIds) {
            ResponseEntity<ApiResponse> responseEntity = merchantClient.isCategoryAssociated(categoryId);
            ApiResponse apiResponse = responseEntity.getBody();
            Boolean isExist = (Boolean) apiResponse.getData();
            if (isExist) {
                associatedCates.add(categoryId);
            }
        }
        if (!associatedCates.isEmpty()) {
            throw new RuntimeException("Categories are already associated with Products - can't be deleted  " + associatedCates);
        }
        for (String catId : catIds) {
            Categories categories = categoriesRepository.findById(catId).orElse(null);
            if (categories != null) {
                availableCats.add(catId);
            } else {
                notAvailableCats.add(catId);
            }
        }
        listMap.put("availableCats", availableCats);
        listMap.put("notAvailableCats", notAvailableCats);
        categoriesRepository.deleteCategory(availableCats);
        return listMap;
    }

    @Override
//    @Transactional(propagation = Propagation.REQUIRED)
    @Transactional
    public Map<String, List<String>> deleteSubCategory(Map<String, List<String>> data) throws Exception {
//        subCategoriesRepository.deleteInBatch(subCategoriesRepository.findAllById(data.get("subcategoryIds")));
        List<String> subCatIds = data.get("subcategoryIds");
//        subCategoriesRepository.findSubCategoryByIsActiveIsTrueAndCategoriesIdIn(subCatIds);
        List<String> availableSubCats = new ArrayList<>();
        List<String> notAvailableSubCats = new ArrayList<>();
        Map<String, List<String>> listMap = new HashMap<>();

        List<String> associatedSubCates = new ArrayList<>();
        for (String subcategoryId : subCatIds) {
            ResponseEntity<ApiResponse> responseEntity = merchantClient.isSubcategoryAssociated(subcategoryId);
            ApiResponse apiResponse = responseEntity.getBody();
            Boolean isExist = (Boolean) apiResponse.getData();
            if (isExist) {
                associatedSubCates.add(subcategoryId);
            }
        }
        if (!associatedSubCates.isEmpty()) {
            throw new RuntimeException("These Subcategories are already associated with Products - can't be deleted " + associatedSubCates);
        }

        for (String subCatId : subCatIds) {
            SubCategories subCategories = subCategoriesRepository.findById(subCatId).orElse(null);
            if (subCategories != null) {
                availableSubCats.add(subCatId);
            } else {
                notAvailableSubCats.add(subCatId);
            }
        }
//        List<String> deletedSubCats = subCategoriesRepository.findByDeletedTrueAndIdIn(subCatIds);
        listMap.put("availableSubCats", availableSubCats);
        listMap.put("notAvailableSubCats", notAvailableSubCats);
//        listMap.put("deletedSubCats",deletedSubCats);
        subCategoriesRepository.deleteSubCategory(availableSubCats);
        return listMap;
    }

    @Override
    public void changeSubCategory(String id, boolean status) {
        Optional<SubCategories> subCategories = subCategoriesRepository.findById(id);
        if (subCategories.isPresent()) {
            subCategories.get().setActive(status);
            subCategories.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            subCategoriesRepository.save(subCategories.get());
        } else {
            throw new ObjectNotFoundException("Sub Category not found for the given id: " + id);
        }
    }

    @Override
    public void changeCategory(String categoryId, boolean status) {
        Optional<Categories> category = categoriesRepository.findById(categoryId);
        if (category.isPresent()) {
            category.get().setActive(status);
            category.get().setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            categoriesRepository.save(category.get());
        } else {
            throw new ObjectNotFoundException("Category not found for the given id: " + categoryId);
        }
    }

    @Override
    public List<CategoriesDTO> getAllCategories(Boolean filterByIsActive, String sortBy, boolean asc) {
        if (asc) {
            return categoriesRepository.findCategoryByIsActive(filterByIsActive, Sort.by(sortBy).ascending())
                    .stream().map(CategoryDataTransformer.CATEGORY_TO_DTO).collect(Collectors.toList());
        } else {
            return categoriesRepository.findCategoryByIsActive(filterByIsActive, Sort.by(sortBy).descending())
                    .stream().map(CategoryDataTransformer.CATEGORY_TO_DTO).collect(Collectors.toList());
        }
    }

    @Override
    public List<SubCategoriesDTO> getSubCategoryByCategoryId(String categoryId, Boolean isActive, String sortBy, boolean asc) {
        if (asc) {
            List<SubCategories> pagedResult = subCategoriesRepository
                    .findSubCategoryByCategoriesIdAndIsActive(categoryId, isActive, Sort.by(sortBy).ascending());
            return pagedResult.stream().map(CategoryDataTransformer.SUBCATEGORY_TO_DTO).collect(Collectors.toList());
        } else {
            List<SubCategories> pagedResult = subCategoriesRepository
                    .findSubCategoryByCategoriesIdAndIsActive(categoryId, isActive, Sort.by(sortBy).descending());
            return pagedResult.stream().map(CategoryDataTransformer.SUBCATEGORY_TO_DTO).collect(Collectors.toList());
        }
    }

    @Override
    public CategoriesDTO getCategoriesByName(String categoryName) {

        Categories categoryByName = categoriesRepository.findCategoryByName(categoryName);
        if (categoryByName != null) {
            return CategoryDataTransformer.CATEGORY_TO_DTO.apply(categoryByName);
        }
        return null;
    }

    @Override
    public SubCategoriesDTO getSubCategoriesByName(String subCategoryName) {
        SubCategories categoryByName = subCategoriesRepository.findSubCategoryByName(subCategoryName);
        if (categoryByName != null) {
            SubCategoriesDTO subCategoriesDTO = CategoryDataTransformer.SUBCATEGORY_TO_DTO.apply(categoryByName);
            return subCategoriesDTO;
        }
        return null;
    }

    private void prepareSubcategoriesSearchQuery(SubcategoryFilterRequest filterRequest, GenericSpecificationsBuilder<SubCategories> builder) {

        builder.with(subCategoriesSpecificationFactory.isEqual("deleted", false));

        if (StringUtils.isNotBlank(filterRequest.getFilterByCategoryName())) {
            Categories categories = categoriesRepository.findCategoryByName(filterRequest.getFilterByCategoryName());
            if (categories == null) {
                throw new ObjectNotFoundException("Subcategory not found for given name: " + filterRequest.getFilterByCategoryName());
            }
            builder.with(subCategoriesSpecificationFactory.isEqual("categories", categories));
        }

        if (StringUtils.isNotBlank(filterRequest.getFilterByName())) {
            builder.with(subCategoriesSpecificationFactory.like("name", filterRequest.getFilterByName()));
        }

        if (filterRequest.getFilterByIsActive() != null) {
            builder.with(subCategoriesSpecificationFactory.isEqual("isActive", filterRequest.getFilterByIsActive()));
        }

    }
}