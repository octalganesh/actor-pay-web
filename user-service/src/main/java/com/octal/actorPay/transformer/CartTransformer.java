package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.CartItemDTO;
import com.octal.actorPay.dto.UserDTO;
import com.octal.actorPay.entities.CartItem;

import java.util.function.Function;

public class CartTransformer {

    public static Function<CartItem, CartItemDTO> CART_ENTITY_TO_CART_DTO = (cartItem) -> {

        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setCartItemId(cartItem.getId());
        UserDTO userDTO = new UserDTO();
        userDTO.setId(cartItem.getUser().getId());
        cartItemDTO.setEmail(cartItem.getUser().getEmail());
        cartItemDTO.setUserDTO(userDTO);
        cartItemDTO.setMerchantId(cartItem.getMerchantId());
        cartItemDTO.setProductCgst(cartItem.getProductCgst());
        cartItemDTO.setProductSgst(cartItem.getProductSgst());
        cartItemDTO.setProductId(cartItem.getProductId());
//        cartItemDTO.setProductName(cartItem.getProductName());
        cartItemDTO.setProductPrice(cartItem.getProductPrice());
        cartItemDTO.setProductQty(cartItem.getProductQty());
        cartItemDTO.setTotalPrice(cartItem.getTotalPrice());
        cartItemDTO.setTaxableValue(cartItem.getTaxableValue());
        cartItemDTO.setShippingCharge(cartItem.getShippingCharge());
        cartItemDTO.setTaxPercentage(cartItem.getTaxPercentage());
        cartItemDTO.setShippingCharge(cartItem.getShippingCharge());
        cartItemDTO.setMerchantId(cartItem.getMerchantId());
//        cartItemDTO.setMerchantName(cartItem.getMerchantName());
        cartItemDTO.setEmail(cartItem.getUser().getEmail());
        cartItemDTO.setCreatedAt(cartItem.getCreatedAt());
        cartItemDTO.setUpdatedAt(cartItem.getUpdatedAt());
        cartItemDTO.setImage(cartItem.getImage());
        cartItemDTO.setActive(cartItem.isActive());
        return cartItemDTO;
    };

    public static Function<CartItemDTO, CartItem> CART_DTO_TO_CART_ENTITY = (cartItemDTO) -> {

        CartItem cartItem = new CartItem();
        cartItem.setMerchantId(cartItemDTO.getMerchantId());
        cartItem.setProductCgst(cartItemDTO.getProductCgst());
        cartItem.setProductSgst(cartItemDTO.getProductSgst());
//        cartItem.setProductName(cartItemDTO.getProductName());
        cartItem.setProductPrice(cartItemDTO.getProductPrice());
        cartItem.setProductQty(cartItemDTO.getProductQty());
        cartItem.setShippingCharge(cartItemDTO.getShippingCharge());
        cartItem.setProductId(cartItemDTO.getProductId());
        cartItem.setImage(cartItemDTO.getImage());
//        cartItem.setProductName(cartItemDTO.getProductName());

        return cartItem;
    };
}
