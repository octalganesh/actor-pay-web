package com.octal.actorPay.listener.events;

import com.octal.actorPay.dto.OfferDTO;
import com.octal.actorPay.entities.User;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

public class AttacheOfferEvent extends ApplicationEvent {

    private final Locale locale;
    private final OfferDTO offerDTO;

    public AttacheOfferEvent(final OfferDTO offerDTO, final Locale locale) {

        super(offerDTO);
        this.offerDTO=offerDTO;
        this.locale=locale;
    }

    public Locale getLocale() {
        return locale;
    }

    public OfferDTO getOfferDTO() {
        return offerDTO;
    }
}
