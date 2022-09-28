package com.octal.actorPay.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class HttpConnector {

    protected WebClient getWebClientObject(String host) {
        WebClient webClientObj = WebClient.create(host);
        return webClientObj;
    }

}
