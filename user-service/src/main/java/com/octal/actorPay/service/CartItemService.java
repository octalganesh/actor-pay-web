package com.octal.actorPay.service;

import com.octal.actorPay.dto.CartDTO;
import com.octal.actorPay.dto.CartItemDTO;
import com.octal.actorPay.dto.ProductDTO;
import com.octal.actorPay.entities.Product;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CartItemService {

    CartDTO addToCart(CartItemDTO cartItemDTO) throws ObjectNotFoundException;
    CartDTO getActiveCartItemByUser(User user);
    CartDTO viewCart(String userName);
    CartDTO updateCart(CartItemDTO cartItemDTOs, String userName);
    CartDTO removeCart(String cartItemId,String userName);
    CartDTO clearCart(String userName);


}
