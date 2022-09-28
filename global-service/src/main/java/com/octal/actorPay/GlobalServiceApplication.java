package com.octal.actorPay;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@EnableEurekaClient
@RestController
public class GlobalServiceApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(GlobalServiceApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO other pre-validations
    }
}