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
@PropertySource(value = "classpath:merchant-service-code.yml", factory = YamlPropertySourceFactory.class)
public class MerchantSettingsConfig {

    private String upiCode;

    public String getUpiCode() {
        return upiCode;
    }

    public void setUpiCode(String upiCode) {
        this.upiCode = upiCode;
    }
}
