package com.octal.actorPay.repositories;

import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.entities.OrderDetails;
import com.octal.actorPay.entities.Product;
import com.octal.actorPay.entities.User;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails,String> , JpaSpecificationExecutor<OrderDetails> {

    OrderDetails findByOrderNoAndDeletedFalse(String orderNo);
    Page<OrderDetails> findByCustomerAndOrderStatusAndDeletedFalse(Pageable pageable, User user, String orderStatus);
    OrderDetails findByOrderNoAndCustomerAndOrderStatus(String oderNo,User customer,String orderStatus);
    OrderDetails findByOrderNoAndCustomer(String oderNo,User customer);
    OrderDetails findByOrderNoAndMerchantId(String oderNo,String merchantId);
    Page<OrderDetails> findByCustomerAndDeletedFalse(Pageable pageable,User user);
    List<OrderDetails> findByCustomerAndDeletedFalse(User user);
    OrderDetails findByOrderNoAndCustomerAndDeletedFalse(String oderNo,User customer);
    OrderDetails findByOrderNoAndMerchantIdAndDeletedFalse(String oderNo,String merchantId);
    List<OrderDetails> findByCustomerAndDeletedFalseAndOrderNoIn(User customer,List<String> oderNo);
    OrderDetails findByCustomerAndDeletedFalseAndOrderNo(User customer,String oderNo);

    @Query("select ord.orderReceipt from OrderDetails ord where ord.orderReceipt = :orderReceipt")
    String findByReceiptId(String orderReceipt);

    @Query("select ord.orderStatus from OrderDetails ord where ord.id=:orderId and ord.deleted=false")
    String getOrderStatusById(@Param("orderId") String orderId);

    @Modifying
    @Query("update OrderDetails ord set ord.orderStatus = :orderStatus where ord.orderNo in (:orderNos) and ord.customer = :user")
    void updateOrderStatus(@Param("orderNos") List<String> orderNos,
                             @Param("orderStatus") String orderStatus,@Param("user") User user);
    @Modifying
    @Query("update OrderDetails ord set ord.orderStatus = :orderStatus where ord.orderNo = :orderNo and ord.customer = :user")
    void updateOrderStatus(@Param("orderNo") String orderNo,
                           @Param("orderStatus") String orderStatus,@Param("user") User user);

    @Modifying
    @Query("update OrderDetails ord set ord.orderStatus = :orderStatus where ord.orderNo = :orderNo")
    void updateOrderStatus(@Param("orderNo") String orderNo,
                           @Param("orderStatus") String orderStatus);

    @Modifying
    @Query("update OrderDetails ord set ord.orderStatus = :orderStatus where ord.orderNo = :orderNo and ord.merchantId = :merchantId")
    void updateOrderStatus(@Param("orderNo") String orderNo,
                           @Param("orderStatus") String orderStatus,@Param("merchantId") String merchantId);
}
