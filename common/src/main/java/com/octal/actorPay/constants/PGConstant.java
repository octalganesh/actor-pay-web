package com.octal.actorPay.constants;

import java.util.Arrays;
import java.util.List;

public interface PGConstant {

    String AMOUNT = "amount";
    String CURRENCY = "currency";
    String RECEIPT = "receipt";
    String PAYMENT_ID = "razorpay_payment_id";
    String ORDER_ID = "razorpay_order_id";
    String SIGNATURE = "razorpay_signature";

    String REFUND_PAYMENT_ID = "payment_id";
    String IND_CURRENCY = "INR";
    String STATUS = "status";
    List<String> RAZOR_PAYMENT_METHODS = Arrays.asList(PaymentMethod.upi.name(),
            PaymentMethod.other.name(), PaymentMethod.card.name(), PaymentMethod.netbanking.name());
}
