package com.octal.actorPay.config;

import com.octal.actorPay.configs.FeignErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public FeignErrorDecoder errorDecoder() {
        System.out.println("##### THIS IS FEIGN ERROR DECODER #####");
        return new FeignErrorDecoder();
    }
}
