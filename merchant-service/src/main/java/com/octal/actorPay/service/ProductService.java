package com.octal.actorPay.service;

import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.ProductDTO;
import com.octal.actorPay.dto.request.ProductFilterRequest;
import com.octal.actorPay.entities.MerchantDetails;
import com.octal.actorPay.entities.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductService {

    ProductDTO save(MultipartFile file, ProductDTO productDTO) throws Exception;

    ProductDTO update(MultipartFile file, ProductDTO productDTO) throws Exception;

    ProductDTO getProductById(String productId);
    ProductDTO findByIdAndMerchantDetails(String productId, MerchantDetails merchantDetails);
    String findProductNameById(String productId);

    PageItem<ProductDTO> getAllProducts(PagedItemInfo pagedInfo, ProductFilterRequest filterRequest);

    Map<String,List<String>> deleteProducts(List<String> productIds,MerchantDetails merchantDetails);

    void changeProductStatus(String id, Boolean status,MerchantDetails merchantDetails);

    Map<String,List<String>> updateProductStatus(List<String> productIds, boolean isActive);

    PageItem<ProductDTO> getAllProductByStatus(PagedItemInfo pagedItemInfo, Boolean status);

    boolean isSubcategoryAssociated(String subcategoryId, Pageable pageable);

    boolean isCategoryAssociated(String categoryId,Pageable pageable);

    ProductDTO findByName(String productName);

    String findByNameAndDeleted(String id,boolean isDeleted);

    void updateProductStock(String productId, Integer stockCount,String stockStatus,MerchantDetails merchantDetails);
    void updateProductStock(String productId, Integer stockCount,String stockStatus);

    long getAllProductProductCount();

}
