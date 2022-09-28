package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.OrderItemCommission;
import com.octal.actorPay.entities.ProductCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderItemCommissionRepository extends JpaRepository<OrderItemCommission,String>, JpaSpecificationExecutor<OrderItemCommission> {
}
