package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.MerchantDetails;
import com.octal.actorPay.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {

    @Query("select product from Product product")
    Page<Product> findAllProducts(Pageable pageable);

    Optional<Product> findByIdAndMerchantDetails(String productId, MerchantDetails merchantDetails);
    @Query("select p from Product p where p.id = :productId")
    Optional<Product> findById(@Param("productId") String productId);

    @Query("select p.name from Product p where p.id = :productId")
    String findProductNameById(@Param("productId") String productId);

    Page<Product> findByIsActive(Pageable pageable, boolean isActive);

    @Modifying
    @Query("delete Product p where p.id in (:productIds)")
    void deleteProducts(@Param("productIds") List<String> productIds);

    @Modifying
    @Query("update Product p set p.isActive= :isActive where p.id in (:productIds)")
    void updateProductStatus(@Param("productIds") List<String> productIds, @Param("isActive") boolean isActive);

    @Modifying
    @Query("update Product p set p.stockCount = :stockCount,p.stockStatus = :stockStatus where p.id = :productId " +
            "and p.merchantDetails = :merchantDetails")
    void updateProductStatus(@Param("productId") String productId,
                             @Param("stockCount") Integer stockCount,
                             @Param("stockStatus") String stockStatus,@Param("merchantDetails") MerchantDetails merchantDetails);

    @Modifying
    @Query("update Product p set p.stockCount = :stockCount,p.stockStatus = :stockStatus where p.id = :productId")
    void updateProductStatus(@Param("productId") String productId,
                             @Param("stockCount") Integer stockCount,
                             @Param("stockStatus") String stockStatus);

//    // excluding deleted records
//    Product findById(String id);

    Product findByName(String productName);

    @Query("select p.id from Product p where p.name = :name")
    String findByProductName(@Param("name") String productName);

    Page<Product> findBySubcategoryId(String subcategoryId, Pageable pageable);

    Page<Product>  findByCategoryId(String categoryId,Pageable pageable);


}
