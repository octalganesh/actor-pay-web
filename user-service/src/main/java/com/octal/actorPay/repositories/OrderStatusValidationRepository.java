package com.octal.actorPay.repositories;


import com.octal.actorPay.entities.OrderStatusValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderStatusValidationRepository extends JpaRepository<OrderStatusValidation,String> {

    @Query("select ord from OrderStatusValidation ord where " +
            "ord.OrderStatusToBeUpdate = :condition and ord.userType = :userType")
    Optional<OrderStatusValidation> findOrderValidation(@Param("condition") String condition, @Param("userType") String userType);


}
