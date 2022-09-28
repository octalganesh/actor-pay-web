package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.CancelOrderItem;
import com.octal.actorPay.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancelOrderItemRepository extends JpaRepository<CancelOrderItem,String> {

    CancelOrderItem findByOrderItemIdAndDeletedFalse(String orderItemId);
}

