package com.octal.actorPay.configs;

import com.octal.actorPay.exceptions.RestClientException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        System.out.println("Status code: {},  methodKey: {} " +  response.status() + " ,  " +methodKey);
        try (InputStream inputStream = response.body().asInputStream()) {
            String responseBody = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines()
                    .collect(Collectors.joining("\n"));
            HttpStatus responseStatus = HttpStatus.valueOf(response.status());
            if (responseStatus.is5xxServerError()) {
                return new RuntimeException(responseBody);
            } else if (responseStatus.is4xxClientError()) {
                return new RestClientException(response.status(), responseBody);
            } else {
                return new Exception("Generic exception");
            }
        } catch (Exception e) {
            System.out.println("Exception occurred in FeignErrorDecoder: {} " + e.getMessage());
        }
        return null;
    }
}
