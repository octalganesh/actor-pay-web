package com.octal.actorPay.utils;

import com.octal.actorPay.dto.PgDetailsDTO;
import com.octal.actorPay.dto.payments.PaymentGatewayResponse;
import org.springframework.stereotype.Component;

@Component
public class PGUtils {

    public PgDetailsDTO buildPgDetails(PaymentGatewayResponse paymentGatewayResponse,
                                       String paymentMethod, String typeId) {

        PgDetailsDTO pgDetailsDTO = new PgDetailsDTO();
        pgDetailsDTO.setPgOrderId(paymentGatewayResponse.getRazorpayOrderId());
        pgDetailsDTO.setPaymentId(paymentGatewayResponse.getRazorpayPaymentId());
        pgDetailsDTO.setPgSignature(paymentGatewayResponse.getRazorpaySignature());
        pgDetailsDTO.setPaymentMethod(paymentMethod);
        pgDetailsDTO.setPaymentTypeId(typeId);
        return pgDetailsDTO;
    }
}
