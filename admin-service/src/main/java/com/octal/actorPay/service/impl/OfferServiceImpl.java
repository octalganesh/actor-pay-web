package com.octal.actorPay.service.impl;

import com.octal.actorPay.constants.OfferVisibility;
import com.octal.actorPay.dto.OfferDTO;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.request.OfferFilterRequest;
import com.octal.actorPay.entities.Categories;
import com.octal.actorPay.entities.Offer;
import com.octal.actorPay.entities.Product;
import com.octal.actorPay.entities.User;
import com.octal.actorPay.exceptions.ExceptionUtils;
import com.octal.actorPay.specification.GenericSpecificationsBuilder;
import com.octal.actorPay.specification.SpecificationFactory;
import com.octal.actorPay.repositories.CategoriesRepository;
import com.octal.actorPay.repositories.OfferRepository;
import com.octal.actorPay.service.OfferService;
import com.octal.actorPay.service.PromoGenerator;
import com.octal.actorPay.transformer.OfferTransformer;
import com.octal.actorPay.transformer.PagedItemsTransformer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OfferServiceImpl implements OfferService {

    private CategoriesRepository categoriesRepository;

    private OfferRepository offerRepository;

    private PromoGenerator promoGenerator;

    private SpecificationFactory<Offer> specificationFactory;

    public OfferServiceImpl(CategoriesRepository categoriesRepository,
                            OfferRepository offerRepository, PromoGenerator promoGenerator, SpecificationFactory specificationFactory) {
        this.categoriesRepository = categoriesRepository;
        this.offerRepository = offerRepository;
        this.promoGenerator = promoGenerator;
        this.specificationFactory = specificationFactory;
    }

    @Override
    public OfferDTO save(OfferDTO offerDTO, User user) {

        Offer offer = null;
        try {
            if (offerDTO == null) {
                throw new RuntimeException("Invalid Request body");
            }
            if (offerDTO.getOfferInPercentage() <= 0 && offerDTO.getOfferInPrice() <= 0) {
                throw new RuntimeException("Either Offer Percentage (OR) Offer Price must be greater than 0");
            }
            if (!StringUtils.isEmpty(offerDTO.getOfferId())) {
                offer = offerRepository.findByIdAndDeletedFalse(offerDTO.getOfferId()).orElse(null);
                offer = OfferTransformer.UPDATE_EXISTING_OFFER.apply(offer, offerDTO);
                offer.setMaxDiscount(offerDTO.getMaxDiscount());
                offer.setUseLimit(offerDTO.getUseLimit());
                offer.setMinOfferPrice(offerDTO.getMinOfferPrice());
                offer.setSingleUserLimit(offerDTO.getSingleUserLimit());
            } else {
                offer = OfferTransformer.OFFER_DTO_TO_OFFER.apply(offerDTO);
                offer.setUser(user);
                offer.setMaxDiscount(offerDTO.getMaxDiscount());
                offer.setUseLimit(offerDTO.getUseLimit());
                offer.setMinOfferPrice(offerDTO.getMinOfferPrice());
                offer.setSingleUserLimit(offerDTO.getSingleUserLimit());
                offer.setOfferCode(promoGenerator.getNewPromoCode());
            }
            Categories categories = categoriesRepository.findById(offerDTO.getCategoryId()).orElse(null);
            offer.setCategories(categories);
            Offer updatedOffer = offerRepository.save(offer);
            OfferDTO updatedOfferDto = OfferTransformer.OFFER_TO_OFFER_DTO.apply(updatedOffer);
            updatedOfferDto.setUserId(user.getId());
            updatedOfferDto.setUserName(user.getEmail());
            updatedOfferDto.setMaxDiscount(updatedOffer.getMaxDiscount());
            updatedOfferDto.setUseLimit(updatedOffer.getUseLimit());
            updatedOfferDto.setMinOfferPrice(updatedOffer.getMinOfferPrice());
            updatedOfferDto.setSingleUserLimit(updatedOffer.getSingleUserLimit());
            return updatedOfferDto;
        } catch (Exception e) {
            if(e instanceof DataIntegrityViolationException) {
                DataIntegrityViolationException dataIntegrityViolationException = (DataIntegrityViolationException)e;
                String errorMessage = ExceptionUtils.duplicateCheckHandleException(dataIntegrityViolationException);
                throw new RuntimeException(errorMessage);
            }else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public PageItem<OfferDTO> getAllOffer(PagedItemInfo pagedInfo, User user, OfferFilterRequest filterRequest) {
        List<OfferDTO> offerDTOList = new ArrayList<>();
        GenericSpecificationsBuilder<Offer> builder = new GenericSpecificationsBuilder<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Offer.class, pagedInfo);
        prepareOfferSearchQuery(filterRequest, builder);
//        Page<Product> pagedResult = productRepository.findAll(builder.build(), pageRequest);
//        Page<Offer> pageResult = offerRepository.findByUserAndDeletedFalse(pageRequest, user);
        Page<Offer> pageResult = offerRepository.findAll(builder.build(), pageRequest);
        for (Offer offer : pageResult) {
            OfferDTO offerDTO = OfferTransformer.OFFER_TO_OFFER_DTO.apply(offer);
            offerDTO.setMaxDiscount(offer.getMaxDiscount());
            offerDTO.setUseLimit(offer.getUseLimit());
            offerDTO.setMinOfferPrice(offer.getMinOfferPrice());
            offerDTO.setSingleUserLimit(offer.getSingleUserLimit());
            offerDTOList.add(offerDTO);
        }
        return new PageItem<>(pageResult.getTotalPages(), pageResult.getTotalElements(), offerDTOList, pagedInfo.page,
                pagedInfo.items);
    }

    @Override
    public OfferDTO getOfferById(String offerId, User user) {
        Offer offer = offerRepository.findByIdAndDeletedFalse(offerId).orElse(null);
        OfferDTO offerDTO = OfferTransformer.OFFER_TO_OFFER_DTO.apply(offer);
        if (offer != null) {
            offerDTO.setMaxDiscount(offer.getMaxDiscount());
            offerDTO.setUseLimit(offer.getUseLimit());
            offerDTO.setMinOfferPrice(offer.getMinOfferPrice());
            offerDTO.setSingleUserLimit(offer.getSingleUserLimit());
        }
        return offerDTO;
    }

    @Override
    public OfferDTO getOfferByPromoCode(String promoCode) {
        Offer offer = offerRepository.findByOfferCodeAndDeleted(promoCode,false).orElse(null);
        OfferDTO offerDTO = OfferTransformer.OFFER_TO_OFFER_DTO.apply(offer);
        if (offer != null) {
            offerDTO.setMaxDiscount(offer.getMaxDiscount());
            offerDTO.setUseLimit(offer.getUseLimit());
            offerDTO.setMinOfferPrice(offer.getMinOfferPrice());
            offerDTO.setSingleUserLimit(offer.getSingleUserLimit());
        }
        return offerDTO;
    }

    @Override
    public OfferDTO getOfferByPromoCodeAndNotDeleted(String promoCode) throws RuntimeException {
        Offer offer = offerRepository.findByOfferCodeAndDeleted(promoCode,false).orElse(null);
        if(offer == null)
            throw new RuntimeException("Invalid Promo Code");
        OfferDTO offerDTO = OfferTransformer.OFFER_TO_OFFER_DTO.apply(offer);
        if (offer != null) {
            offerDTO.setMaxDiscount(offer.getMaxDiscount());
            offerDTO.setUseLimit(offer.getUseLimit());
            offerDTO.setMinOfferPrice(offer.getMinOfferPrice());
            offerDTO.setSingleUserLimit(offer.getSingleUserLimit());
        }
        return offerDTO;
    }

    @Override
    public OfferDTO getOfferByTitle(String offerTitle, User user) {
        Offer offer = offerRepository.findByOfferTitleAndUserAndDeletedFalse(offerTitle, user).orElse(null);
        OfferDTO offerDTO = OfferTransformer.OFFER_TO_OFFER_DTO.apply(offer);
        return offerDTO;
    }

    @Override
    public void deleteOfferById(String offerId, User user) {
        offerRepository.deleteOffer(offerId, user);
    }

    @Override
    public PageItem<OfferDTO> getAvailableOffers(PagedItemInfo pagedInfo, boolean isActive,
                                                 String visibilityLevel,OfferFilterRequest filterRequest) {
        List<OfferDTO> offerDTOList = new ArrayList<>();
//        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Offer.class, pagedInfo);
//        Page<Offer> pageResult = offerRepository.findOfferByIsActiveAndVisibilityLevelAndDeletedFalse
//                (pageRequest, isActive, OfferVisibility.CATEGORY_LEVEL.name());
        GenericSpecificationsBuilder<Offer> builder = new GenericSpecificationsBuilder<>();
        final PageRequest pageRequest = PagedItemsTransformer.toPageRequest(Offer.class, pagedInfo);
        prepareOfferSearchQuery(filterRequest, builder);
        Page<Offer> pageResult = offerRepository.findAll(builder.build(), pageRequest);
        for (Offer offer : pageResult) {
            OfferDTO offerDTO = OfferTransformer.OFFER_TO_OFFER_DTO.apply(offer);
            offerDTO.setMaxDiscount(offer.getMaxDiscount());
            offerDTO.setUseLimit(offer.getUseLimit());
            offerDTO.setMinOfferPrice(offer.getMinOfferPrice());
            offerDTO.setSingleUserLimit(offer.getSingleUserLimit());
            offerDTOList.add(offerDTO);
        }
        return new PageItem<>(pageResult.getTotalPages(), pageResult.getTotalElements(), offerDTOList, pagedInfo.page,
                pagedInfo.items);
    }


//    public void activateOrDeactivateOffer(List<String> offerIds, boolean isActivate, User user) {
//        offerRepository.activateOrDeActivate(offerIds, isActivate, user);
//    }
    @Transactional
    @Override
    public Map<String, List<String>> activateOrDeactivateOffer(List<String> offerIds, boolean isActive,User user) {
        List<String> products = new ArrayList<>();
        List<String> availableOfferIds = new ArrayList<>();
        List<String> notAvailableOfferIds = new ArrayList<>();
        Map<String, List<String>> listMap = new HashMap<>();
        for (String offerId : offerIds) {
            Offer offer = offerRepository.findByIdAndDeletedFalse(offerId).orElse(null);
            if (offer != null) {
                availableOfferIds.add(offerId);
            } else {
                notAvailableOfferIds.add(offerId);
            }
        }
        listMap.put("availableOffers", availableOfferIds);
        listMap.put("notAvailableOffers", notAvailableOfferIds);
        offerRepository.activateOrDeActivate(availableOfferIds,isActive,user);
        return listMap;
    }

    @Override
    public void updateOfferUsageCount(OfferDTO offerDTO) {
       Offer offer = offerRepository.findById(offerDTO.getOfferId()).orElse(null);
       if (offer != null) {
           offer.setNumberOfUsage(offer.getNumberOfUsage());
           offerRepository.save(offer);
       }
    }


    private void prepareOfferSearchQuery(OfferFilterRequest filterRequest, GenericSpecificationsBuilder<Offer> builder) {

        String serviceName = filterRequest.getServiceName();
        builder.with(specificationFactory.isEqual("deleted", false));
        if(StringUtils.isNotEmpty(serviceName) && serviceName.equalsIgnoreCase("userService")) {
            builder.with(specificationFactory.isEqual("isActive", true));
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(filterRequest.getOfferTitle())) {
            builder.with(specificationFactory.like("offerTitle", filterRequest.getOfferTitle()));
        }
        if (filterRequest.getActive() != null) {
            builder.with(specificationFactory.isEqual("isActive", filterRequest.getActive()));
        }
        if (filterRequest.getOfferInPercentage() != null && filterRequest.getOfferInPercentage() > 0) {
            builder.with(specificationFactory.isEqual("offerInPercentage", filterRequest.getOfferInPercentage()));
        }
        if (filterRequest.getOfferInPrice() != null && filterRequest.getOfferInPrice() > 0) {
            builder.with(specificationFactory.isEqual("offerInPrice", filterRequest.getOfferInPrice()));
        }
        if (StringUtils.isNotEmpty(filterRequest.getOfferType())) {
            builder.with(specificationFactory.isEqual("offerType", filterRequest.getOfferType()));
        }
        if (filterRequest.getNumberOfUsage() != null && filterRequest.getNumberOfUsage() > 0) {
            builder.with(specificationFactory.isEqual("numberOfUsage", filterRequest.getNumberOfUsage()));
        }
        if (filterRequest.getOrdersPerDay() != null && filterRequest.getOrdersPerDay() > 0) {
            builder.with(specificationFactory.isEqual("offerCode", filterRequest.getPromoCode()));
        }
        if (StringUtils.isNotEmpty(filterRequest.getPromoCode())) {
            builder.with(specificationFactory.isEqual("ordersPerDay", filterRequest.getOrdersPerDay()));
        }
        if (StringUtils.isNotEmpty(filterRequest.getCategories())) {
            Categories categories = categoriesRepository.findCategoryByName(filterRequest.getCategories());
            builder.with(specificationFactory.isEqual("categories", categories));
        }
        if (filterRequest.getOfferStartDateFrom() != null) {
            builder.with(specificationFactory.isGreaterThan("offerStartDate", filterRequest.getOfferStartDateFrom().atStartOfDay()));
        }
        if (filterRequest.getOfferStartDateTo() != null) {
            builder.with(specificationFactory.isLessThan("offerStartDate", filterRequest.getOfferStartDateTo().plusDays(1).atStartOfDay()));
        }
        if (filterRequest.getOfferEndDateFrom() != null) {
            builder.with(specificationFactory.isGreaterThan("offerEndDate", filterRequest.getOfferEndDateFrom().atStartOfDay()));
        }
        if (filterRequest.getOfferEndDateTo() != null) {
            builder.with(specificationFactory.isLessThan("offerEndDate", filterRequest.getOfferEndDateTo().plusDays(1).atStartOfDay()));
        }

            if (filterRequest.getCreatedDateFrom() != null) {
            builder.with(specificationFactory.isGreaterThan("createdAt", filterRequest.getCreatedDateFrom().atStartOfDay()));
        }

        if (filterRequest.getCreatedDateTo() != null) {
            builder.with(specificationFactory.isLessThan("createdAt", filterRequest.getCreatedDateTo().plusDays(1).atStartOfDay()));
        }

        if (filterRequest.getMaxDiscount() != null && filterRequest.getMaxDiscount() > 0) {
            builder.with(specificationFactory.isLessThan("maxDiscount", filterRequest.getMaxDiscount()));
        }

        if (filterRequest.getUseLimit() != null && filterRequest.getUseLimit() > 0) {
            builder.with(specificationFactory.isLessThan("useLimit", filterRequest.getUseLimit()));
        }

        if (filterRequest.getMinOfferPrice() != null && filterRequest.getMinOfferPrice() > 0) {
            builder.with(specificationFactory.isLessThan("minimumOfferPrice", filterRequest.getMinOfferPrice()));
        }
    }

}
