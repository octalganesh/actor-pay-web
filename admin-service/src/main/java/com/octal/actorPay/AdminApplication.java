package com.octal.actorPay;

import com.octal.actorPay.entities.Screens;
import com.octal.actorPay.entities.Screens;
import com.octal.actorPay.interceptor.AdminFeignClientInterceptor;
import com.octal.actorPay.repositories.ScreensRepository;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@EnableScheduling
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@EnableEurekaClient
@RestController
@EnableFeignClients
@ComponentScan({"com.octal.actorPay"})
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new AdminFeignClientInterceptor();
    }
}

/**
 * This insert the data into Screens table in DB before starting the service.
 */
@Component
class ModulesCommandLineRunner implements CommandLineRunner {

    @Autowired
    private ScreensRepository screensRepository;

    @Override
    public void run(String... args) throws Exception {

        if(screensRepository.count() == 0) {

            List<Screens> screens= new ArrayList<>();
            screens.add(new Screens("User Management",null,1));
            screens.add(new Screens("Category Management",null,2));
            screens.add(new Screens( "Sub-Category Management",null,3));
            screens.add(new Screens( "Transaction",null,4));
            screens.add(new Screens( "Loyalty Reward Pointsv",null,5));
            screens.add(new Screens( "Money Transfer limit",null,6));
            screens.add(new Screens( "Earning  Manager",null,7));
            screens.add(new Screens( "Product Manager",null,8));
            screens.add(new Screens( "Email Template",null,9));
            screens.add(new Screens( "Content Management",null,10));
            screens.add(new Screens( "FAQ Management",null,11));
            screens.add(new Screens( "Global Setting",null,12));

            screensRepository.saveAll(screens);
        }
    }
}