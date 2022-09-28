package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.CancelOrder;
import com.octal.actorPay.entities.OrderDetails;
import com.octal.actorPay.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CancelOrderRepository extends JpaRepository<CancelOrder, String> {

    Optional<CancelOrder> findByOrderDetailsAndDeletedFalse(OrderDetails orderDetails);

    @Query("select crd from CancelOrder crd where crd.orderDetails in (select ord.id from OrderDetails ord where " +
            "ord.customer = :user and ord.deleted = false)")
    Page<CancelOrder> findCancelOrdersByUser(Pageable pageable, @Param("user") User user);

    @Query("select crd from CancelOrder crd where crd.orderDetails = :orderDetails and crd.deleted = false")
    CancelOrder findCancelOrdersByOrderId(@Param("orderDetails") OrderDetails orderDetails);
}
