package com.octal.actorPay.controller;

import com.octal.actorPay.dto.payments.QrCodeCreditRequest;
import com.octal.actorPay.service.WebHookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class PGWebHookController {

    @Autowired
    WebHookService webHookService;

    @PostMapping("/v1/webhook/qr/credit")
    public void webHookQrCodePaymentCredit(@RequestBody QrCodeCreditRequest creditRequest, HttpServletRequest request) throws Exception {
        webHookService.qrCodePaymentCredit(creditRequest);
    }
}
