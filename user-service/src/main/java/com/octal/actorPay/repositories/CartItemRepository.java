package com.octal.actorPay.repositories;

import com.octal.actorPay.dto.ProductDTO;
import com.octal.actorPay.entities.CartItem;
import com.octal.actorPay.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, String> {

    List<CartItem> findByUserAndIsActiveTrueAndDeletedFalse(User suer);
    CartItem findByProductIdAndIsActiveTrueAndUserAndDeletedFalse(String productId,User user);
    CartItem findByIdAndUserAndIsActiveTrueAndDeletedFalse(String cartItemId,User user);

    @Modifying
    @Query("update CartItem c set c.deleted=true,c.isActive=false where c.isActive=true and c.user=:user and c.id =:cartItemId")
    void deleteByIdAndUserAndIsActiveTrueAndDeletedFalse(@Param("cartItemId") String cartItemId, @Param("user") User user);

    @Modifying
    @Query("update CartItem c set c.deleted=true,c.isActive=false where c.isActive=true and c.user=:user and c.id in :cartItemIds")
    void deleteCartItem(@Param("cartItemIds") List<String> cartItemIds, @Param("user") User user);

}
