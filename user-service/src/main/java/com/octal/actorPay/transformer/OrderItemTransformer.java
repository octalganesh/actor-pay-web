package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.CancelOrderItemDTO;
import com.octal.actorPay.dto.CartItemDTO;
import com.octal.actorPay.dto.OrderItemDTO;
import com.octal.actorPay.dto.OrderNoteDTO;
import com.octal.actorPay.entities.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

public class OrderItemTransformer {


    public static Function<CartItemDTO,OrderItem> CART_ITEM_DTO_TO_ORDER_ITEM = (cartItemDTO) -> {

        OrderItem orderItem = new OrderItem();
        orderItem.setProductCgst(cartItemDTO.getProductCgst());
        orderItem.setProductSgst(cartItemDTO.getProductSgst());
        orderItem.setProductId(cartItemDTO.getProductId());
        orderItem.setProductPrice(cartItemDTO.getProductPrice());
        orderItem.setProductQty(cartItemDTO.getProductQty());
        orderItem.setShippingCharge(cartItemDTO.getShippingCharge());
        orderItem.setTaxableValue(cartItemDTO.getTaxableValue());
        orderItem.setTotalPrice(cartItemDTO.getTotalPrice());
        orderItem.setTaxPercentage(cartItemDTO.getTaxPercentage());
        orderItem.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        orderItem.setMerchantId(cartItemDTO.getMerchantId());
        orderItem.setImage(cartItemDTO.getImage());
        orderItem.setActive(Boolean.TRUE);
        return orderItem;
    };

    public static Function<OrderItem, OrderItemDTO> ORDER_ITEM_TO_ORDER_ITEM_DTO = (orderItem) -> {

        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setOrderItemId(orderItem.getId());
        orderItemDTO.setProductCgst(orderItem.getProductCgst());
        orderItemDTO.setProductSgst(orderItem.getProductSgst());
        orderItemDTO.setProductId(orderItem.getProductId());
        orderItemDTO.setProductPrice(orderItem.getProductPrice());
        orderItemDTO.setActualPrice(orderItem.getActualPrice());
        orderItemDTO.setProductQty(orderItem.getProductQty());
        orderItemDTO.setShippingCharge(orderItem.getShippingCharge());
        orderItemDTO.setTaxableValue(orderItem.getTaxableValue());
        orderItemDTO.setTotalPrice(orderItem.getTotalPrice());
        orderItemDTO.setTaxPercentage(orderItem.getTaxPercentage());
        orderItemDTO.setCreatedAt(orderItem.getCreatedAt());
        orderItemDTO.setUpdatedAt(orderItem.getUpdatedAt());
        orderItemDTO.setActive(orderItem.isActive());
        orderItemDTO.setCategoryId(orderItem.getCategoryId());
        orderItemDTO.setSubcategoryId(orderItem.getSubcategoryId());
//        orderItemDTO.setMerchantName(orderItem.getMerchantName());
        orderItemDTO.setMerchantId(orderItem.getMerchantId());
//        orderItemDTO.setProductName(orderItem.getProductName());
        orderItemDTO.setImage(orderItem.getImage());
        orderItemDTO.setOrderItemStatus(orderItem.getOrderItemStatus());
        return orderItemDTO;
    };

    public static Function<OrderItemDTO,OrderItem> ORDER_ITEM_DTO_TO_ENTITY = (orderItemDTO) -> {

        OrderItem orderItem = new OrderItem();
        orderItem.setId(orderItemDTO.getOrderItemId());
        orderItem.setProductCgst(orderItemDTO.getProductCgst());
        orderItem.setProductSgst(orderItemDTO.getProductSgst());
        orderItem.setProductId(orderItemDTO.getProductId());
        orderItem.setProductPrice(orderItemDTO.getProductPrice());
        orderItem.setProductQty(orderItemDTO.getProductQty());
        orderItem.setShippingCharge(orderItemDTO.getShippingCharge());
        orderItem.setTaxableValue(orderItemDTO.getTaxableValue());
        orderItem.setTotalPrice(orderItemDTO.getTotalPrice());
        orderItem.setTaxPercentage(orderItemDTO.getTaxPercentage());
        orderItem.setCreatedAt(orderItemDTO.getCreatedAt());
        orderItem.setUpdatedAt(orderItemDTO.getUpdatedAt());
        orderItem.setActive(orderItemDTO.getActive());
        orderItem.setCategoryId(orderItemDTO.getCategoryId());
        orderItem.setSubcategoryId(orderItemDTO.getSubcategoryId());
//        orderItemDTO.setMerchantName(orderItem.getMerchantName());
        orderItemDTO.setMerchantId(orderItemDTO.getMerchantId());
//        orderItemDTO.setProductName(orderItem.getProductName());
        orderItemDTO.setImage(orderItemDTO.getImage());
        return orderItem;
    };

    public static Function<CancelOrderItem, CancelOrderItemDTO> CANCEL_ORDER_ITEM_TO_DTO = (cancelOrderItem) -> {

        CancelOrderItemDTO cancelOrderItemDTO = new CancelOrderItemDTO();
        cancelOrderItemDTO.setId(cancelOrderItem.getId());
        cancelOrderItemDTO.setOrderItemId(cancelOrderItem.getOrderItemId());
        cancelOrderItemDTO.setRefundAmount(cancelOrderItem.getRefundAmount());
        cancelOrderItemDTO.setActive(cancelOrderItem.getActive());
        cancelOrderItemDTO.setCreatedAt(cancelOrderItem.getCreatedAt());
        cancelOrderItemDTO.setUpdatedAt(cancelOrderItem.getUpdatedAt());
        return cancelOrderItemDTO;
    };

    public static Function<OrderNoteDTO,OrderNote> ORDER_NOTE_DTO_TO_ENTITY = (orderNoteDTO) ->{

        OrderNote orderNote = new OrderNote();
        orderNote.setId(orderNoteDTO.getOrderNoteId());
        orderNote.setUserId(orderNoteDTO.getUserId());
        orderNote.setOrderNoteBy(orderNoteDTO.getOrderNoteBy());
        orderNote.setUserType(orderNoteDTO.getUserType());
        orderNote.setOrderNoteDescription(orderNoteDTO.getOrderNoteDescription());
        orderNote.setActive(Boolean.TRUE);
        orderNote.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        orderNote.setMerchantId(orderNoteDTO.getMerchantId());
        return orderNote;
    };

    public static Function<OrderNote,OrderNoteDTO> ENTITY_TO_ORDER_NOTE_DTO = (orderNote) ->{

        OrderNoteDTO orderNoteDTO = new OrderNoteDTO();
        orderNoteDTO.setOrderNoteId(orderNote.getId());
        orderNoteDTO.setUserId(orderNote.getUserId());
        orderNoteDTO.setOrderNoteBy(orderNote.getOrderNoteBy());
        orderNoteDTO.setUserType(orderNote.getUserType());
        orderNoteDTO.setOrderNoteDescription(orderNote.getOrderNoteDescription());
        orderNoteDTO.setActive(orderNote.getActive());
        orderNoteDTO.setCreatedAt(orderNote.getCreatedAt());
        orderNoteDTO.setOrderNo(orderNote.getOrderDetails().getOrderNo());
        orderNoteDTO.setOrderId(orderNote.getOrderDetails().getId());

        orderNoteDTO.setMerchantId(orderNote.getMerchantId());
        return orderNoteDTO;
    };
}
