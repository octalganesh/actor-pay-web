package com.octal.actorPay.transformer;

import com.octal.actorPay.dto.OutletDto;
import com.octal.actorPay.entities.Address;
import com.octal.actorPay.entities.Outlet;

import java.util.Objects;
import java.util.function.Function;

public class OutletTransformer {

    public static Function<Outlet, OutletDto> OUTLET_TO_DTO = (o) -> {
        OutletDto outletDto = new OutletDto();
        outletDto.setExtensionNumber(o.getExtensionNumber());
        outletDto.setContactNumber(o.getContactNumber());
        outletDto.setLicenceNumber(o.getLicenceNumber());
        outletDto.setResourceType(o.getResourceType());
        outletDto.setId(o.getId());
        outletDto.setTitle(o.getTitle());
        outletDto.setDescription(o.getDescription());
        outletDto.setCreatedAt(o.getCreatedAt());
        outletDto.setActive(o.isActive());
        if (Objects.nonNull(o.getAddress())) {
            Address address = o.getAddress();
            outletDto.setAddressLine1(address.getAddressLine1());
            outletDto.setAddressLine2(address.getAddressLine2());
            outletDto.setCity(address.getCity());
            outletDto.setState(address.getState());
            outletDto.setCountry(address.getCountry());
            outletDto.setLatitude(address.getLatitude());
            outletDto.setLongitude(address.getLongitude());
            outletDto.setZipCode(address.getZipCode());
        }

        return outletDto;

    };
}
