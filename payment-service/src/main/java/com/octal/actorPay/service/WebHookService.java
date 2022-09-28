package com.octal.actorPay.service;

import com.octal.actorPay.dto.payments.QrCodeCreditRequest;
import org.springframework.stereotype.Service;

@Service
public interface WebHookService {
    void qrCodePaymentCredit(QrCodeCreditRequest request) throws Exception;
}
