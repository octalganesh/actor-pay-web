package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.ShippingAddressDTO;
import com.octal.actorPay.entities.ShippingAddress;

import java.util.function.Function;

public class ShipmentAddressTransformer {

    public static Function<ShippingAddress, ShippingAddressDTO> SHIPPING_ADDRESS_TO_DTO = (shipping) -> {
        ShippingAddressDTO shippingAddressDTO = new ShippingAddressDTO();
        shippingAddressDTO.setName(shipping.getName());
        shippingAddressDTO.setPrimaryContactNumber(shipping.getPrimaryContactNumber());
        shippingAddressDTO.setSecondaryContactNumber(shipping.getSecondaryContactNumber());
        shippingAddressDTO.setAddressLine1(shipping.getAddressLine1());
        shippingAddressDTO.setAddressLine2(shipping.getAddressLine2());
        shippingAddressDTO.setCity(shipping.getCity());
        shippingAddressDTO.setState(shipping.getState());
        shippingAddressDTO.setCountry(shipping.getCountry());
        shippingAddressDTO.setLatitude(shipping.getLatitude());
        shippingAddressDTO.setLongitude(shipping.getLongitude());
        shippingAddressDTO.setZipCode(shipping.getZipCode());
        shippingAddressDTO.setId(shipping.getId());
        shippingAddressDTO.setPrimary(shipping.getPrimary());
        shippingAddressDTO.setSecondaryContactNumber(shipping.getSecondaryContactNumber());
        shippingAddressDTO.setExtensionNumber(shipping.getExtensionNumber());
        shippingAddressDTO.setAddressTitle(shipping.getAddressTitle());
        shippingAddressDTO.setArea(shipping.getArea());
        return shippingAddressDTO;
    };

}
