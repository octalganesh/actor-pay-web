package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.OutletDto;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.request.OutletFilterRequest;
import com.octal.actorPay.entities.Address;
import com.octal.actorPay.entities.Outlet;
import com.octal.actorPay.entities.Role;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ActorPayException;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.repositories.MerchantDetailsRepository;
import com.octal.actorPay.repositories.OutletRepository;
import com.octal.actorPay.repositories.UserRepository;
import com.octal.actorPay.service.OutletService;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.transformer.OutletTransformer;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OutletServiceImpl implements OutletService {

    @Autowired
    private OutletRepository outletRepository;

    @Autowired
    private MerchantDetailsRepository merchantDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpecificationFactory<Outlet> outletSpecificationFactory;


    @Override
    public @NotBlank String create(@NotNull @Valid OutletDto dto, String actor) {

        Optional<User> currentUser = userRepository.findByEmail(actor);
        if (currentUser.isPresent()) {
//            List<String> roles = currentUser.get().getRoles().stream().map(Role::getName).collect(Collectors.toList());
            Role role = currentUser.get().getRole();
            if (role.getName().equals(Role.RoleName.PRIMARY_MERCHANT.name())) {
                Outlet outlet = new Outlet();
                setOutletProperties(outlet, dto);
                outlet.setMerchantDetails(currentUser.get().getMerchantDetails());
                Address address = new Address();
                mapOutletAddress(address, dto);
                address.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
                outlet.setAddress(address);
                outlet.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
                outlet.setActive(Boolean.TRUE);
                Outlet outletObj = outletRepository.save(outlet);
                return outletObj.getId();
            } else {
                throw new ActorPayException("Access denied!!, Only merchant can create outlet " + actor);
            }
        } else {
            throw new ActorPayException("User not found for email " + actor);
        }
    }

    @Override
    public void update(@NotBlank String id, @NotNull @Valid OutletDto dto) {
        Outlet outlet = outletRepository.getOne(dto.getId());
        if (Objects.nonNull(outlet)) {
            setOutletProperties(outlet, dto);
            Address address = mapOutletAddress(outlet.getAddress(), dto);
            outlet.setAddress(address);
            outletRepository.save(outlet);
        }
    }

    private void setOutletProperties(Outlet outlet, OutletDto dto) {
        outlet.setLicenceNumber(dto.getLicenceNumber());
        outlet.setResourceType(dto.getResourceType());
        outlet.setTitle(dto.getTitle());
        outlet.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        outlet.setExtensionNumber(dto.getExtensionNumber());
        outlet.setContactNumber(dto.getContactNumber());
        outlet.setDescription(dto.getDescription());

    }

    private Address mapOutletAddress(Address address, OutletDto dto) {
        if (address == null) {
            address = new Address();
        }
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setCountry(dto.getCountry());
        address.setLatitude(dto.getLatitude());
        address.setLongitude(dto.getLongitude());
        address.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        address.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        address.setZipCode(dto.getZipCode());
        address.setActive(dto.getActive());
        address.setState(dto.getState());
        return address;
    }

    @Override
    public OutletDto read(String id, String actor) {
        Outlet outlet = outletRepository.findByIdAndDeletedFalse(id).orElseThrow(()-> new ActorPayException("Outlet Id not found"));
        if (Objects.nonNull(outlet)) {
            return OutletTransformer.OUTLET_TO_DTO.apply(outlet);
        }
        return null;
    }

    @Override
    public List<OutletDto> list(String actor) {
        return null;

    }

    @Override
    public PageItem<OutletDto> listWithPagination(PagedItemInfo pagedInfo, OutletFilterRequest filterRequest, String actor) {
        User currentUser = userRepository.findByEmail(actor).orElseThrow(()-> new ActorPayException("User not found"));
        if (currentUser != null) {
            GenericSpecificationsBuilder<Outlet> builder = new GenericSpecificationsBuilder<>();

            final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Outlet.class, pagedInfo);
            prepareOutletSearchQuery(filterRequest, currentUser.getMerchantDetails().getId(), builder);
            Page<Outlet> pagedResult = outletRepository.findAll(builder.build(), pageRequest);

            List<OutletDto> content = pagedResult.getContent().stream().map(OutletTransformer.OUTLET_TO_DTO).collect(Collectors.toList());

            return new PageItem<>(pagedResult.getTotalPages(), pagedResult.getTotalElements(), content, pagedInfo.page, pagedInfo.items);
        }

        return null;
    }

    @Override
    public Map delete(List<String> ids, String currentUser) {
        Map<String, Object> data = new HashMap<>();
        for (String id : ids) {
            if (outletRepository.findById(id).isPresent()) {
                outletRepository.deleteById(id);
                data.put(id, "Deleted Successfully");
            } else {
                data.put(id, "Outlet Id not found");
            }
        }
        return data;
    }

    @Override
    public void changeOutletStatus(String id, Boolean status) {
        Outlet outlet = outletRepository.findByIdAndDeletedFalse(id).orElseThrow(()-> new ActorPayException("Outlet not found"));
        if (Objects.nonNull(outlet)) {
            outlet.setActive(status);
            outlet.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
            outletRepository.save(outlet);
        } else {
            throw new ObjectNotFoundException("Outlet not found for the given id: " + id);
        }
    }

    @Override
    public Long findCountById(String outletId) {
        return outletRepository.countById(outletId);
    }

    @Override
    public Boolean delete(@NotBlank String id) {
        return null;
    }

    private void prepareOutletSearchQuery(OutletFilterRequest filterRequest, String merchantId, GenericSpecificationsBuilder<Outlet> builder) {

        builder.with(outletSpecificationFactory.isEqual("deleted", false));

        if (StringUtils.isNotBlank(merchantId)) {
            builder.with(outletSpecificationFactory.join("merchantDetails", "id", merchantId));
        }

        if (StringUtils.isNotBlank(filterRequest.getName())) {
            builder.with(outletSpecificationFactory.like("name", filterRequest.getName()));
        }

        if (StringUtils.isNotBlank(filterRequest.getLicenceNumber())) {
            builder.with(outletSpecificationFactory.like("licenceNumber", filterRequest.getLicenceNumber()));
        }

        if (StringUtils.isNotBlank(filterRequest.getContactNumber())) {
            builder.with(outletSpecificationFactory.isEqual("contactNumber", filterRequest.getContactNumber()));
        }

        if (StringUtils.isNotBlank(filterRequest.getTitle())) {
            builder.with(outletSpecificationFactory.like("title", filterRequest.getTitle()));
        }

        if (StringUtils.isNotBlank(filterRequest.getDescription())) {
            builder.with(outletSpecificationFactory.like("description", filterRequest.getDescription()));
        }
        if (filterRequest.getStatus() != null) {
            builder.with(outletSpecificationFactory.isEqual("isActive", filterRequest.getStatus()));
        }

        if (filterRequest.getStartDate() != null) {
            builder.with(outletSpecificationFactory.isGreaterThan("createdAt", filterRequest.getStartDate().atStartOfDay()));
        }
        if (filterRequest.getEndDate() != null) {
            builder.with(outletSpecificationFactory.isLessThan("createdAt", filterRequest.getEndDate().plusDays(1).atStartOfDay()));
        }

    }

}
