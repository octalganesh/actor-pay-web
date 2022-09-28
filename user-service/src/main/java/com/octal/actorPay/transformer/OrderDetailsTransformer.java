package com.octal.actorPay.transformer;

import com.octal.actorPay.constants.OrderStatus;
import com.octal.actorPay.dto.*;
import com.octal.actorPay.entities.*;
import org.springframework.core.annotation.Order;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Function;

public class OrderDetailsTransformer {


    public static Function<CartDTO, OrderDetails> CART_DTO_TO_ORDER_DETAILS = (cartDTO) -> {

        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setOrderStatus(OrderStatus.SUCCESS.name());
        orderDetails.setMerchantId(cartDTO.getMerchantId());
//        orderDetails.setMerchantName(cartDTO.getMerchantName());
        orderDetails.setTotalCgst(cartDTO.getTotalCgst());
        orderDetails.setTotalSgst(cartDTO.getTotalSgst());
        orderDetails.setTotalQuantity(cartDTO.getTotalQuantity());
        orderDetails.setTotalTaxableValue(cartDTO.getTotalTaxableValue());
        orderDetails.setTotalPrice(cartDTO.getTotalPrice());
        orderDetails.setTotalPriceAfterPromo(cartDTO.getTotalPrice());
        return orderDetails;
    };

    public static Function<OrderDetails, OrderDetailsDTO> ORDER_DETAILS_TO_ORDER_DETAILS_DTO = (orderDetails) -> {

        OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
        orderDetailsDTO.setOrderId(orderDetails.getId());
        orderDetailsDTO.setOrderNo(orderDetails.getOrderNo());
        orderDetailsDTO.setOrderStatus(orderDetails.getOrderStatus());
        orderDetailsDTO.setMerchantId(orderDetails.getMerchantId());
//        orderDetailsDTO.setMerchantName(orderDetails.getMerchantName());
        orderDetailsDTO.setTotalCgst(orderDetails.getTotalCgst());
        orderDetailsDTO.setTotalSgst(orderDetails.getTotalSgst());
        orderDetailsDTO.setTotalQuantity(orderDetails.getTotalQuantity());
        orderDetailsDTO.setTotalTaxableValue(orderDetails.getTotalTaxableValue());
        orderDetailsDTO.setTotalPrice(orderDetails.getTotalPrice());
        orderDetailsDTO.setCreatedAt(orderDetails.getCreatedAt());
        orderDetailsDTO.setUpdatedAt(orderDetails.getUpdatedAt());
        orderDetailsDTO.setPaymentMethod(orderDetails.getPaymentMethod());
        return orderDetailsDTO;
    };
    public static Function<OrderDetailsDTO, OrderDetails> ORDER_DETAILS_DTO_ORDER_DETAILS_ENTITY = (orderDetailsDTO) -> {

        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setId(orderDetailsDTO.getOrderId());
        orderDetails.setOrderNo(orderDetailsDTO.getOrderNo());
        orderDetails.setOrderStatus(orderDetailsDTO.getOrderStatus());
        orderDetails.setMerchantId(orderDetailsDTO.getMerchantId());
        orderDetails.setTotalCgst(orderDetailsDTO.getTotalCgst());
        orderDetails.setTotalSgst(orderDetailsDTO.getTotalSgst());
        orderDetails.setTotalQuantity(orderDetailsDTO.getTotalQuantity());
        orderDetails.setTotalTaxableValue(orderDetailsDTO.getTotalTaxableValue());
        orderDetails.setTotalPrice(orderDetailsDTO.getTotalPrice());
        orderDetails.setCreatedAt(orderDetailsDTO.getCreatedAt());
        orderDetails.setUpdatedAt(orderDetailsDTO.getUpdatedAt());
        orderDetails.setPaymentMethod(orderDetailsDTO.getPaymentMethod());
        return orderDetails;
    };

    public static Function<CancelOrder, CancelOrderDTO> CANCEL_ORDER_TO_CANCEL_ORDER_DTO = (cancelOrder) -> {

        CancelOrderDTO cancelOrderDTO = new CancelOrderDTO();
        cancelOrderDTO.setCancelReason(cancelOrder.getCancelReason());
        cancelOrderDTO.setImage(cancelOrder.getImage());
        cancelOrderDTO.setRefundAmount(cancelOrder.getRefundAmount());
//        cancelOrderDTO.setCreatedAt(cancelOrder.getCreatedAt());
//        cancelOrderDTO.setCancellationRequest(cancelOrder.getCancellationRequest());
        return cancelOrderDTO;
    };

    public static Function<ShippingAddressDTO, ShippingAddress> SHIPPING_DTO_TO_SHIPPING_ENTITY = (shippingAddressDTO) -> {
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setId(shippingAddressDTO.getId());
        shippingAddress.setName(shippingAddressDTO.getName());
        shippingAddress.setPrimary(shippingAddressDTO.getPrimary());
        shippingAddress.setPrimaryContactNumber(shippingAddressDTO.getPrimaryContactNumber());
        shippingAddress.setSecondaryContactNumber(shippingAddressDTO.getSecondaryContactNumber());
        shippingAddress.setAddressLine1(shippingAddressDTO.getAddressLine1());
        shippingAddress.setAddressLine2(shippingAddressDTO.getAddressLine2());
        shippingAddress.setCity(shippingAddressDTO.getCity());
        shippingAddress.setState(shippingAddressDTO.getState());
        shippingAddress.setCountry(shippingAddressDTO.getCountry());
        shippingAddress.setAddressTitle(shippingAddressDTO.getAddressTitle());
        shippingAddress.setArea(shippingAddressDTO.getArea());
        shippingAddress.setExtensionNumber(shippingAddressDTO.getExtensionNumber());
        shippingAddress.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        if (shippingAddressDTO.getPrimary() == null) {
            shippingAddress.setPrimary(false);
        }
        return shippingAddress;
    };

    public static Function<ShippingAddress, ShippingAddressDTO> SHIPPING_ENTITY_TO_SHIPPING_DTO = (shippingAddress) -> {
        ShippingAddressDTO shippingAddressDto = new ShippingAddressDTO();
        shippingAddressDto.setId(shippingAddress.getId());
        shippingAddressDto.setName(shippingAddress.getName());
        shippingAddressDto.setPrimary(shippingAddress.getPrimary());
        shippingAddressDto.setPrimaryContactNumber(shippingAddress.getPrimaryContactNumber());
        shippingAddressDto.setSecondaryContactNumber(shippingAddress.getSecondaryContactNumber());
        shippingAddressDto.setAddressLine1(shippingAddress.getAddressLine1());
        shippingAddressDto.setAddressLine2(shippingAddress.getAddressLine2());
        shippingAddressDto.setCity(shippingAddress.getCity());
        shippingAddressDto.setState(shippingAddress.getState());
        shippingAddressDto.setCountry(shippingAddress.getCountry());
        return shippingAddressDto;
    };

    public static Function<OrderAddressDTO, OrderAddress> ORDER_ADDR_DTO_TO_ORDER_ADDR_ENTITY = (orderAddressDTO) -> {
        OrderAddress orderAddress = new OrderAddress();
        orderAddress.setId(orderAddressDTO.getId());
        orderAddress.setName(orderAddressDTO.getName());
        orderAddress.setPrimaryContactNumber(orderAddressDTO.getPrimaryContactNumber());
        orderAddress.setSecondaryContactNumber(orderAddressDTO.getSecondaryContactNumber());
        orderAddress.setAddressLine1(orderAddressDTO.getAddressLine1());
        orderAddress.setAddressLine2(orderAddressDTO.getAddressLine2());
        orderAddress.setCity(orderAddressDTO.getCity());
        orderAddress.setState(orderAddressDTO.getState());
        orderAddress.setCountry(orderAddressDTO.getCountry());
        orderAddress.setAddressTitle(orderAddressDTO.getAddressTitle());
        orderAddress.setArea(orderAddressDTO.getArea());
        orderAddress.setExtensionNumber(orderAddressDTO.getExtensionNumber());
        orderAddress.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        orderAddress.setProfileAddressId(orderAddressDTO.getId());
        if (orderAddressDTO.getPrimary() == null) {
            orderAddress.setPrimary(false);
        }else{
            orderAddress.setPrimary(orderAddressDTO.getPrimary());
        }
        orderAddress.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        orderAddress.setDeleted(false);
        orderAddress.setZipCode(orderAddressDTO.getZipCode());
        orderAddress.setActive(true);
        return orderAddress;
    };

    public static Function<ShippingAddressDTO, OrderAddress> SHIPPING_DTO_TO_ORDER_ADDR_ENTITY = (shippingAddressDTO) -> {
        OrderAddress orderAddress = new OrderAddress();
        orderAddress.setId(shippingAddressDTO.getId());
        orderAddress.setName(shippingAddressDTO.getName());
        orderAddress.setPrimaryContactNumber(shippingAddressDTO.getPrimaryContactNumber());
        orderAddress.setSecondaryContactNumber(shippingAddressDTO.getSecondaryContactNumber());
        orderAddress.setAddressLine1(shippingAddressDTO.getAddressLine1());
        orderAddress.setAddressLine2(shippingAddressDTO.getAddressLine2());
        orderAddress.setCity(shippingAddressDTO.getCity());
        orderAddress.setState(shippingAddressDTO.getState());
        orderAddress.setCountry(shippingAddressDTO.getCountry());
        orderAddress.setAddressTitle(shippingAddressDTO.getAddressTitle());
        orderAddress.setArea(shippingAddressDTO.getArea());
        orderAddress.setExtensionNumber(shippingAddressDTO.getExtensionNumber());
        orderAddress.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        orderAddress.setProfileAddressId(shippingAddressDTO.getId());
        if (shippingAddressDTO.getPrimary() == null) {
            orderAddress.setPrimary(false);
        }else{
            orderAddress.setPrimary(shippingAddressDTO.getPrimary());
        }
        orderAddress.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        orderAddress.setDeleted(false);
        orderAddress.setZipCode(shippingAddressDTO.getZipCode());
        orderAddress.setActive(true);
        return orderAddress;
    };

    public static Function<OrderAddress, OrderAddressDTO> ORDER_ADDR_ENTITY_TO_ORDER_ADDR_DTO = (orderAddress) -> {
        OrderAddressDTO orderAddressDTO = new OrderAddressDTO();
        orderAddressDTO.setId(orderAddress.getId());
        orderAddressDTO.setName(orderAddress.getName());
        orderAddressDTO.setPrimary(orderAddress.getPrimary());
        orderAddressDTO.setPrimaryContactNumber(orderAddress.getPrimaryContactNumber());
        orderAddressDTO.setSecondaryContactNumber(orderAddress.getSecondaryContactNumber());
        orderAddressDTO.setAddressLine1(orderAddress.getAddressLine1());
        orderAddressDTO.setAddressLine2(orderAddress.getAddressLine2());
        orderAddressDTO.setCity(orderAddress.getCity());
        orderAddressDTO.setState(orderAddress.getState());
        orderAddressDTO.setCountry(orderAddress.getCountry());
        orderAddressDTO.setProfileAddressId(orderAddress.getProfileAddressId());
        orderAddressDTO.setCreatedAt(orderAddress.getCreatedAt());
        orderAddressDTO.setUpdatedAt(orderAddress.getUpdatedAt());
        orderAddressDTO.setDeleted(orderAddress.isDeleted());
        orderAddressDTO.setZipCode(orderAddress.getZipCode());
        orderAddressDTO.setName(orderAddress.getName());
        orderAddressDTO.setExtensionNumber(orderAddress.getExtensionNumber());
        orderAddressDTO.setPrimary(orderAddress.getPrimary());
        orderAddressDTO.setAddressTitle(orderAddress.getAddressTitle());
        orderAddressDTO.setArea(orderAddress.getArea());
        orderAddressDTO.setActive(orderAddress.getActive());
        return orderAddressDTO;
    };

    public static Function<OrderNote, OrderNoteDTO> ORDER_NOTE_ENTITY_TO_DTO = (orderNote) ->{

        OrderNoteDTO orderNoteDTO = new OrderNoteDTO();
        orderNoteDTO.setUserId(orderNote.getUserId());
        orderNoteDTO.setOrderNoteBy(orderNote.getOrderNoteBy());
        orderNoteDTO.setUserType(orderNote.getUserType());
        orderNoteDTO.setCreatedAt(orderNote.getCreatedAt());
        orderNoteDTO.setUpdatedAt(orderNote.getUpdatedAt());
        orderNoteDTO.setOrderNoteDescription(orderNote.getOrderNoteDescription());
        orderNoteDTO.setMerchantId(orderNote.getMerchantId());
        orderNoteDTO.setOrderId(orderNote.getOrderDetails().getId());
        orderNoteDTO.setOrderNo(orderNote.getOrderDetails().getOrderNo());
        orderNoteDTO.setOrderNoteId(orderNote.getId());
        orderNoteDTO.setActive(orderNote.getActive());

        return orderNoteDTO;
    };


}
