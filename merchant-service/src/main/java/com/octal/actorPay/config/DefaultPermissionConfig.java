package com.octal.actorPay.config;

import com.octal.actorPay.utils.LoadDefaultPermission;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultPermissionConfig {

    @Bean(initMethod = "loadDefaultPermissions")
    public LoadDefaultPermission LoadDefaultPermission() {
        return new LoadDefaultPermission();
    }

}
