package com.octal.actorPay.service.impl;

import com.octal.actorPay.dto.ApplyPromoCodeResponse;
import com.octal.actorPay.dto.OfferDTO;
import com.octal.actorPay.dto.PromoCodeDetailsDTO;
import com.octal.actorPay.dto.request.PromoCodeFilter;
import com.octal.actorPay.entities.PromoCodesHistory;
import com.octal.actorPay.repositories.PromoCodesHistoryRepository;
import com.octal.actorPay.service.OfferService;
import com.octal.actorPay.service.PromoCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author - Nishant Saraswat
 * this class contain all the logics for promo codes
 */

@Service
public class PromoCodeImpl implements PromoCodeService {

    @Autowired
    private OfferService offerService;

    @Autowired
    private PromoCodesHistoryRepository promoCodesHistoryRepository;

    @Override
    public ApplyPromoCodeResponse applyPromoCode(PromoCodeFilter filterRequest) throws RuntimeException {
        ApplyPromoCodeResponse response = new ApplyPromoCodeResponse();
        OfferDTO offer = offerService.getOfferByPromoCodeAndNotDeleted(filterRequest.getPromoCode());
        if (!offer.getIsActive()) {
            throw new RuntimeException("Offer is not active");
        }

        if (offer.getOfferEndDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Promo code is already expired");
        }

        List<PromoCodesHistory> promoCodesHistory = promoCodesHistoryRepository.findByPromoCode(filterRequest.getPromoCode());

        if (Objects.nonNull(promoCodesHistory) && promoCodesHistory.size() >= offer.getUseLimit()) {
            throw new RuntimeException("Use limit is completed");
        }

        List<PromoCodesHistory> promoCodesHistoriesByUser = promoCodesHistory.stream()
                .filter(v -> v.getUserId().equals(filterRequest.getUserId())).collect(Collectors.toList());

        if (promoCodesHistoriesByUser.size() >= offer.getSingleUserLimit()) {
            throw new RuntimeException("Single user limit is completed");
        }

        if (offer.getMinOfferPrice() > filterRequest.getAmount()) {
            throw new RuntimeException("Amount is less than minimum offer amount");
        }

        float discount = 0;
        float amountAfterDiscount = filterRequest.getAmount();
        if (offer.getOfferInPercentage() > 0) {
            discount = (filterRequest.getAmount() * offer.getOfferInPercentage()) / 100;
            if (discount > offer.getMaxDiscount()) {
                discount = offer.getMaxDiscount();
            }
            amountAfterDiscount = filterRequest.getAmount() - discount;
        } else if (offer.getOfferInPrice() > 0) {
            discount = offer.getOfferInPrice();
            amountAfterDiscount = filterRequest.getAmount() - discount;
        }
        response.setAmount(filterRequest.getAmount());
        response.setDiscount(discount);
        response.setAmountAfterDiscount(amountAfterDiscount);
        response.setPromoCode(filterRequest.getPromoCode());
        return response;
    }

    @Override
    public void savePromoCode(List<ApplyPromoCodeResponse> request) {
        request.forEach(
                v -> {
                    OfferDTO offer = offerService.getOfferByPromoCode(v.getPromoCode());
                    PromoCodesHistory promoCodesHistory = new PromoCodesHistory();
                    promoCodesHistory.setPromoCode(v.getPromoCode());
                    promoCodesHistory.setUserId(v.getUserId());
                    promoCodesHistory.setDiscountAmount(v.getDiscount());
                    promoCodesHistory.setOrderId(v.getOrderId());
                    promoCodesHistory.setOrderItemId(v.getOrderItemId());
                    promoCodesHistoryRepository.save(promoCodesHistory);
                    offer.setNumberOfUsage(offer.getNumberOfUsage() + 1);
                    offerService.updateOfferUsageCount(offer);
                });
    }

    @Override
    public List<PromoCodeDetailsDTO> getPromoCodeHistory(String orderId) {
        List<PromoCodeDetailsDTO> responseList = new ArrayList<>();
        for(PromoCodesHistory promoCodesHistory : promoCodesHistoryRepository.findByOrderId(orderId)){
            PromoCodeDetailsDTO response = new PromoCodeDetailsDTO();
            response.setPromoCode(promoCodesHistory.getPromoCode());
            response.setOrderId(promoCodesHistory.getOrderId());
            response.setDiscountAmount(promoCodesHistory.getDiscountAmount());
            response.setUserId(promoCodesHistory.getUserId());
            responseList.add(response);
        }
        return responseList;
    }
}
