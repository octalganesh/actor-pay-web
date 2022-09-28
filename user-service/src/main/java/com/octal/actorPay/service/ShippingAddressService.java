package com.octal.actorPay.service;


import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.ShippingAddressDTO;
import org.springframework.stereotype.Component;

@Component
public interface ShippingAddressService {

    ShippingAddressDTO saveShipmentAddress(String currentUser, ShippingAddressDTO shipmentAddressDTO);

    void updateShippingAddress(String currentUser, ShippingAddressDTO shipmentAddressDTO);

    ShippingAddressDTO getShippingAddressDetails(String currentUser, String shippingAddressId);

    PageItem<ShippingAddressDTO> getAllUserSavedShippingAddress(PagedItemInfo pagedInfo, String currentUser);

    void deleteShippingAddress(String id, String actor);

    long availableShippingAddress(String currentUser);
}
