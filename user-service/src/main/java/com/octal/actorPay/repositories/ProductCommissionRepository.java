package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.OrderItem;
import com.octal.actorPay.entities.Product;
import com.octal.actorPay.entities.ProductCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductCommissionRepository extends
        JpaRepository<ProductCommission,String>, JpaSpecificationExecutor<ProductCommission> {

    ProductCommission findByOrderItemAndDeletedFalse(OrderItem orderItem);

    @Modifying
    @Query("update ProductCommission p set p.settlementStatus=:settlementStatus,p.orderStatus = :orderStatus where p.id in (:ids) and p.deleted=false")
    void updateProductCommissionStatus(@Param("settlementStatus") String settlementStatus,
                                       @Param("orderStatus") String orderStatus,
                                       @Param("ids") List<String> ids);
   List<ProductCommission> findByIdIn(List<String> ids);
    List<ProductCommission> findBySettlementStatus(String settlementStatus);


    @Query(value = "SELECT SUM(p.actor_commission_amt) FROM product_commission p where p.order_status = :orderStatus", nativeQuery = true)
    Float totalAdminCommissionByOrderStatus(@Param("orderStatus") String orderStatus);

}
