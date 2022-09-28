package com.octal.actorPay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.octal.actorPay.repositories"})
@ComponentScan("com.octal.actorPay")
@EnableFeignClients
public class ChatApplication {
    public static void main( String[] args )
    {
        SpringApplication.run(ChatApplication.class, args);
    }
}
