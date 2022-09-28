package com.octal.actorPay.repositories;

import com.octal.actorPay.entities.ShippingAddress;
import com.octal.actorPay.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, String> {

    Page<ShippingAddress> findShippingAddressByUserId(String userId, Pageable pageRequest);
    ShippingAddress findByIdAndDeletedFalse(String id);
    @Query("select count(*) from ShippingAddress s where s.user.id = :userId")
    long findShippingAddressByUserIdAndDeletedFalse(@Param("userId") String userId);
    Optional<ShippingAddress> findShippingAddressByUserIdAndPrimary(String userId,Boolean primary);

}
