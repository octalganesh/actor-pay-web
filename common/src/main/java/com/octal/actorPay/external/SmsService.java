package com.octal.actorPay.external;


import com.octal.actorPay.dto.request.SmsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SmsService {


    @Value("${external.sms.service.url}")
    private String smsServiceUrl;


    public ResponseEntity<?> sendSMS(
            SmsRequest request) {
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("Authorization", "8pgZ12-YR6Kktt1JFZ43xw==");

        return restTemplate.exchange(smsServiceUrl, HttpMethod.POST, new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<Object>() {
                });
    }


    public ResponseEntity<?> sendSMSNotification(String content, List<String> to) {
        SmsRequest smsRequest = new SmsRequest();
        smsRequest.setContent(content);
        smsRequest.setTo(to);
        return sendSMS(smsRequest);
    }
}
