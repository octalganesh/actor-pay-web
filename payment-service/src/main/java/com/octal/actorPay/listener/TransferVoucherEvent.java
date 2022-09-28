package com.octal.actorPay.listener;

import com.octal.actorPay.constants.OfferType;
import org.springframework.context.ApplicationEvent;

public class TransferVoucherEvent extends ApplicationEvent {

    private final OfferType offerType;


    public TransferVoucherEvent(final OfferType offerType) {
        super(offerType);
        this.offerType = offerType;
    }

    public OfferType getOfferType() {
        return offerType;
    }
}
