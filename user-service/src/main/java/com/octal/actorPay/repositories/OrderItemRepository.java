package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.OrderDetails;
import com.octal.actorPay.entities.OrderItem;
import com.octal.actorPay.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem,String> {

    OrderItem findByIdAndDeletedFalse(String id);
    @Modifying
    @Query("update OrderItem item set item.orderItemStatus = :orderStatus " +
            "where item.orderDetails = :orderDetails and item.id in (:orderItemIds)")
    void updateOrderStatus(@Param("orderDetails") OrderDetails orderDetails,
                           @Param("orderItemIds") List<String> orderIds,
                           @Param("orderStatus") String orderStatus);

    @Modifying(clearAutomatically = true)
    @Query("update OrderItem item set item.orderItemStatus = :orderStatus " +
            "where item.orderDetails = :orderDetails and item.id = :orderItemId")
    void updateOrderStatusByOrderItemId(@Param("orderDetails") OrderDetails orderDetails,
                           @Param("orderItemId") String orderItemId,
                           @Param("orderStatus") String orderStatus);

    List<OrderItem> findByDeletedFalseAndIdIn(List<String> ids);

}
