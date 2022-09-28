package com.octal.actorPay.service;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.dto.payments.RefundRequest;

public interface PaymentService {

    ApiResponse doRefund(RefundRequest refundRequest);
}
