package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.ShippingAddressDTO;
import com.octal.actorPay.entities.ShippingAddress;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.repositories.ShippingAddressRepository;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.CommonService;
import com.octal.actorPay.service.ShippingAddressService;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import com.octal.actorPay.transformer.ShipmentAddressTransformer;
import com.octal.actorPay.validator.RuleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class ShippingAddressServiceImpl implements ShippingAddressService {


    @Autowired
    private ShippingAddressRepository shipmentAddressRepository;

    @Autowired
    private RuleValidator ruleValidator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommonService commonService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ShippingAddressDTO saveShipmentAddress(String currentUserEmail, ShippingAddressDTO shipmentAddressDTO) {

        User user = ruleValidator.checkPresence(userRepository.findByEmail(currentUserEmail).get(), "User not found : " + currentUserEmail);
        Optional<ShippingAddress> primaryAddressOpt = shipmentAddressRepository
                .findShippingAddressByUserIdAndPrimary(user.getId(), true);
        if (primaryAddressOpt.isPresent()) {
            if (shipmentAddressDTO.getPrimary() != null) {
                if (shipmentAddressDTO.getPrimary().booleanValue()) {
//                    throw new RuntimeException("Primary Address must be one - Already Primary Address exist");
                    primaryAddressOpt.get().setPrimary(false);
                    shipmentAddressRepository.save(primaryAddressOpt.get());
                }
            } else {
                shipmentAddressDTO.setPrimary(false);
            }
        }
        ShippingAddress shippingAddress = new ShippingAddress();
        mapShippingAddressObject(shipmentAddressDTO, shippingAddress);
        shippingAddress.setUser(user);
        shippingAddress.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        shippingAddress.setActive(Boolean.TRUE);
        shippingAddress.setDeleted(Boolean.FALSE);
        shippingAddress.setPrimary(shipmentAddressDTO.getPrimary());
        long count  = shipmentAddressRepository.findShippingAddressByUserIdAndDeletedFalse(user.getId());
        if(count == 0) {
            shippingAddress.setPrimary(true);
        }
        ShippingAddress savedObject = shipmentAddressRepository.save(shippingAddress);
        return ShipmentAddressTransformer.SHIPPING_ADDRESS_TO_DTO.apply(savedObject);

    }

    @Override
    public void updateShippingAddress(String currentUser, ShippingAddressDTO shipmentAddressDTO) {
        Optional<User> user = commonService.findUserByEmail(currentUser);
        Optional<ShippingAddress> shippingAddress = ruleValidator.checkPresence(shipmentAddressRepository.findById(shipmentAddressDTO.getId()), "Shipping address not found for given id: " + shipmentAddressDTO.getId());
        if (shippingAddress.isPresent()) {
//            if (!shipmentAddressDTO.getId().equals(shippingAddress.get().getId())) {
//                throw new ActorPayException("Access Denied, Only account owner can perform this operation");
//            }
            Optional<ShippingAddress> primaryAddressOpt = shipmentAddressRepository.findShippingAddressByUserIdAndPrimary(user.get().getId(), true);

            if (primaryAddressOpt.isPresent()) {
                if (!primaryAddressOpt.get().getId().equals(shipmentAddressDTO.getId())) {
                    if (shipmentAddressDTO.getPrimary() != null) {
                        if (shipmentAddressDTO.getPrimary().booleanValue()) {
                            primaryAddressOpt.get().setPrimary(false);
                            shipmentAddressRepository.save(primaryAddressOpt.get());
                        }
                    }
                }else{
                    if (shipmentAddressDTO.getPrimary().booleanValue() == false) {
                        throw new RuntimeException("One shipping address is Mandatory");
                    }
                }
            }
            mapShippingAddressObject(shipmentAddressDTO, shippingAddress.get());
            shipmentAddressRepository.save(shippingAddress.get());
        }
    }

    @Override
    public ShippingAddressDTO getShippingAddressDetails(String currentUser, String shippingAddressId) {
        Optional<ShippingAddress> shippingAddress = ruleValidator.checkPresence(shipmentAddressRepository.findById(shippingAddressId), "Shipping address not found for given id: " + shippingAddressId);
        Optional<User> user = commonService.findUserByEmail(currentUser);

        if (shippingAddress.isPresent()) {
            if (!shippingAddress.get().getUser().getId().equals(user.get().getId())) {
                throw new ActorPayException("Access Denied, Only account owner can perform this operation");
            }
            return ShipmentAddressTransformer.SHIPPING_ADDRESS_TO_DTO.apply(shippingAddress.get());
        }
        return null;
    }

    @Override
    public PageItem<ShippingAddressDTO> getAllUserSavedShippingAddress(PagedItemInfo pagedInfo, String currentUser) {
        Optional<User> user = commonService.findUserByEmail(currentUser);


        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(ShippingAddress.class, pagedInfo);

        Page<ShippingAddress> pagedResult = shipmentAddressRepository.findShippingAddressByUserId(user.get().getId(), pageRequest);
        List<ShippingAddressDTO> content = pagedResult.getContent().stream().map(ShipmentAddressTransformer.SHIPPING_ADDRESS_TO_DTO)
                .collect(Collectors.toList());

        return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), content, pagedInfo.page,
                pagedInfo.items);
    }

    @Override
    public void deleteShippingAddress(String id, String actor) {
        User user = userRepository.getUserDetailsByEmilOrContactNO(actor);
        long count = shipmentAddressRepository.findShippingAddressByUserIdAndDeletedFalse(user.getId());
        if (count == 0) {
            throw new RuntimeException("No Address found in the Profile");
        }
        if (count == 1) {
            throw new RuntimeException("Can't delete the Address. At least one address must maintain");
        }
        Optional<ShippingAddress> shippingAddressOpt = shipmentAddressRepository.findById(id);
        if (shippingAddressOpt.isPresent()) {
            ShippingAddress shippingAddress = shippingAddressOpt.get();
            if (shippingAddress.getPrimary()) {
                throw new RuntimeException("Primary Address can't be delete");
            }
            shipmentAddressRepository.delete(shippingAddress);
        } else {
            throw new RuntimeException(String.format("No address found for given id %s", id));
        }
    }

    @Override
    public long availableShippingAddress(String currentUser) {
        return shipmentAddressRepository.findShippingAddressByUserIdAndDeletedFalse(currentUser);
    }

    private void mapShippingAddressObject(ShippingAddressDTO shipmentAddressDTO, ShippingAddress shippingAddress) {
        shippingAddress.setName(shipmentAddressDTO.getName());
        shippingAddress.setExtensionNumber(shipmentAddressDTO.getExtensionNumber());
        shippingAddress.setPrimaryContactNumber(shipmentAddressDTO.getPrimaryContactNumber());
        shippingAddress.setSecondaryContactNumber(shipmentAddressDTO.getSecondaryContactNumber());
        shippingAddress.setAddressLine1(shipmentAddressDTO.getAddressLine1());
        shippingAddress.setAddressLine2(shipmentAddressDTO.getAddressLine2());
        shippingAddress.setCity(shipmentAddressDTO.getCity());
        shippingAddress.setState(shipmentAddressDTO.getState());
        shippingAddress.setCountry(shipmentAddressDTO.getCountry());
        shippingAddress.setLatitude(shipmentAddressDTO.getLatitude());
        shippingAddress.setLongitude(shipmentAddressDTO.getLongitude());
        shippingAddress.setZipCode(shipmentAddressDTO.getZipCode());
        shippingAddress.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        shippingAddress.setPrimary(shipmentAddressDTO.getPrimary());
        shippingAddress.setArea(shipmentAddressDTO.getArea());
        shippingAddress.setAddressTitle(shipmentAddressDTO.getAddressTitle());
    }

}
