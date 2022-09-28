package com.octal.actorPay.controller;

import com.octal.actorPay.external.SmsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/sms")
public class SMSTestController {

    private SmsService smsService;

    public SMSTestController(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendSMS(@RequestParam("mobileNo") String mobileNo,
                                          @RequestParam("msg") String msg) {
        try {
            smsService.sendSMSNotification(msg, Arrays.asList(mobileNo));
            return new ResponseEntity<>("Message Send Successfully ", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Not able to send message", HttpStatus.BAD_REQUEST);
        }
    }
}
