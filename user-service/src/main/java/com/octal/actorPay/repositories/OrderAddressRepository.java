package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderAddressRepository extends JpaRepository<OrderAddress,String> {
}
