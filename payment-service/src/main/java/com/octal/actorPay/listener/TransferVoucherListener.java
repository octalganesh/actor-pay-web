package com.octal.actorPay.listener;

import com.octal.actorPay.constants.OfferType;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class TransferVoucherListener implements ApplicationListener<TransferVoucherEvent> {


    @Override
    public void onApplicationEvent(TransferVoucherEvent event) {

        OfferType offerType = event.getOfferType();
        System.out.println("Your PromoCode is here :  "+offerType);
    }
}
