package com.octal.actorPay.service;

import com.octal.actorPay.entities.Offer;
import com.octal.actorPay.repositories.OfferRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PromoGenerator {

    private OfferRepository offerRepository;

    public PromoGenerator(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public String getNewPromoCode() {
        String promoCode = "";
        Offer offer = null;
        do {
            String newPromoCode = getPromoCode();
            offer = offerRepository.findByOfferCodeAndDeletedFalse(newPromoCode).orElse(null);
            if (offer == null) {
                promoCode = newPromoCode;
            }
        } while (offer != null);
        return promoCode;
    }

    private String getPromoCode() {
        int length = 6;
        String promoCode = RandomStringUtils.random(length, true, false).toUpperCase();
        System.out.println(promoCode);
        return promoCode;
    }
}
