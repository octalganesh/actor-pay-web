package com.octal.actorPay.config;


import com.octal.actorPay.utils.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@Configuration
@ConfigurationProperties("system-code")
@PropertySource(value = "classpath:payment-service-code.yml", factory = YamlPropertySourceFactory.class)
public class PaymentSettingsConfig {

    private String walletCode;
    private String parentWalletCode;
    private String requestMoneyCode;

}
