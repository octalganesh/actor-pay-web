package com.octal.actorPay.service;

import com.octal.actorPay.dto.OfferDTO;
import com.octal.actorPay.dto.PageItem;
import com.octal.actorPay.dto.PagedItemInfo;
import com.octal.actorPay.dto.request.OfferFilterRequest;
import com.octal.actorPay.entities.Offer;
import com.octal.actorPay.entities.User;

import java.util.List;
import java.util.Map;

public interface OfferService {

    OfferDTO save(OfferDTO offerDTO,User user);

    PageItem<OfferDTO> getAllOffer(PagedItemInfo pagedInfo, User user, OfferFilterRequest filterRequest);
    OfferDTO getOfferById(String offerId,User user);
    OfferDTO getOfferByPromoCode(String promoCode);
    OfferDTO getOfferByPromoCodeAndNotDeleted(String promoCode) throws RuntimeException;
    OfferDTO getOfferByTitle(String offerTitle,User user);
    void deleteOfferById(String offerId,User user);
    PageItem<OfferDTO> getAvailableOffers(PagedItemInfo pagedInfo,boolean isPublished,String visibilityLevel, OfferFilterRequest filterRequest);
    Map<String, List<String>> activateOrDeactivateOffer(List<String> offerIds, boolean isActivate, User user);

    void updateOfferUsageCount(OfferDTO offerDTO);

}
