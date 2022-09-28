package com.octal.actorPay.controller;

import com.octal.actorPay.dto.payments.QrCodeCreditRequest;
import com.octal.actorPay.service.UserPGService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/v1/webhook")
public class UserWebHookController {

    @Autowired
    private UserPGService userPGService;

    @PostMapping("/qr/credit")
    public void webHookQrCodePaymentCredit(@RequestBody QrCodeCreditRequest creditRequest, HttpServletRequest request) throws Exception {
        userPGService.qrCodePaymentCredit(creditRequest);
    }
}
