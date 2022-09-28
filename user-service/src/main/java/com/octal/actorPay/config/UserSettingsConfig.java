package com.octal.actorPay.config;


import com.octal.actorPay.utils.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties("system-code")
@PropertySource(value = "classpath:userservice-code.yml", factory = YamlPropertySourceFactory.class)
public class UserSettingsConfig {

    public String orderCode;
    public String disputeCode;
    public String orderReceipt;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getDisputeCode() {
        return disputeCode;
    }

    public void setDisputeCode(String disputeCode) {
        this.disputeCode = disputeCode;
    }

    public String getOrderReceipt() {
        return orderReceipt;
    }

    public void setOrderReceipt(String orderReceipt) {
        this.orderReceipt = orderReceipt;
    }
}
