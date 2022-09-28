package com.octal.actorPay.listener;

import com.octal.actorPay.dto.OfferDTO;
import com.octal.actorPay.listener.events.AttacheOfferEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class OfferListener implements ApplicationListener<AttacheOfferEvent> {


    @Override
    @Async
    public void onApplicationEvent(AttacheOfferEvent event) {
        final OfferDTO offerDTO = event.getOfferDTO();
        System.out.println("Offer Title  " + offerDTO.getOfferTitle());
        System.out.println("Offer Description " + offerDTO.getOfferDescription());
        System.out.println("Offer Added to the Product");
    }
}