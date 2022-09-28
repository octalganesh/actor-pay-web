package com.octal.actorPay.transformer;

import com.octal.actorPay.constants.OfferType;
import com.octal.actorPay.dto.OfferDTO;
import com.octal.actorPay.entities.Offer;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.BiFunction;
import java.util.function.Function;

public class OfferTransformer {

    public static Function<Offer, OfferDTO> OFFER_TO_OFFER_DTO = (offer) -> {

        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setOfferId(offer.getId());
        offerDTO.setOfferTitle(offer.getOfferTitle());
        offerDTO.setOfferDescription(offer.getOfferDescription());
        offerDTO.setOfferInPrice(offer.getOfferInPrice());
        offerDTO.setOfferInPercentage(offer.getOfferInPercentage());
        offerDTO.setCreatedAt(offer.getCreatedAt());
        offerDTO.setUpdatedAt(offer.getUpdatedAt());

        offerDTO.setOfferCode(offer.getOfferCode());
        offerDTO.setOfferType(offer.getOfferType().name());
        offerDTO.setIsActive(offer.getActive());
        offerDTO.setNumberOfUsage(offer.getNumberOfUsage());
        offerDTO.setOrdersPerDay(offer.getOrdersPerDay());
        if(offer.getCategories() != null)
            offerDTO.setCategoryId(offer.getCategories().getId());
        offerDTO.setVisibilityLevel(offer.getVisibilityLevel());
        offerDTO.setOfferStartDate(offer.getOfferStartDate());
        offerDTO.setOfferEndDate(offer.getOfferEndDate());
        return offerDTO;
    };

    public static BiFunction<Offer,OfferDTO,Offer> UPDATE_EXISTING_OFFER = (offer,offerDTO) -> {
//        offerDTO.setOfferId(offer.getId());
        offer.setOfferTitle(offerDTO.getOfferTitle());
        offer.setOfferDescription(offerDTO.getOfferDescription());
        offer.setOfferInPrice(offerDTO.getOfferInPrice());
        offer.setOfferInPercentage(offerDTO.getOfferInPercentage());
        offer.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        offer.setOfferStartDate(offerDTO.getOfferStartDate());
        offer.setOfferEndDate(offerDTO.getOfferEndDate());

        offer.setOfferType(OfferType.valueOf(offerDTO.getOfferType()));
       // offer.setOfferType(offerDTO.getOfferType());
//        offer.setActive(offerDTO.getActive());
        offer.setNumberOfUsage(offerDTO.getNumberOfUsage());
        offer.setOrdersPerDay(offerDTO.getOrdersPerDay());
        offer.setVisibilityLevel(offerDTO.getVisibilityLevel());
        return offer;
    };
    public static Function<OfferDTO, Offer> OFFER_DTO_TO_OFFER = (offerDTO) -> {
        Offer offer = null;
        if (StringUtils.isEmpty(offerDTO.getOfferId())) {
            offer = new Offer();
        } else {
        }

        //offer.setId(offerDTO.getOfferId());
        offer.setOfferType(OfferType.valueOf(offerDTO.getOfferType()));
        offer.setOfferInPrice(offerDTO.getOfferInPrice());
        offer.setOfferTitle(offerDTO.getOfferTitle());
        offer.setOfferEndDate(offerDTO.getOfferEndDate());
        offer.setOfferStartDate(offerDTO.getOfferStartDate());
        offer.setOfferInPercentage(offerDTO.getOfferInPercentage());
        offer.setNumberOfUsage(offerDTO.getNumberOfUsage());
        offer.setOrdersPerDay(offerDTO.getOrdersPerDay());
        offer.setOfferDescription(offerDTO.getOfferDescription());

        offer.setOfferCode(offerDTO.getOfferCode());
        offer.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        offer.setVisibilityLevel(offerDTO.getVisibilityLevel());
        offer.setActive(true);
        return offer;
    };

}