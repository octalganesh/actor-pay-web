package com.octal.actorPay.configs;


import com.octal.actorPay.utils.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties("global-settings")
@PropertySource(value = "classpath:globalsettings.yml", factory = YamlPropertySourceFactory.class)
public class GlobalSettingsConfig {


    private String globalSettings;

    @Autowired
    private Environment env;

    public String getConfig(String key) {
        return env.getProperty(getFullKey(key));
    }

    private String getFullKey(String key) {
        String fullKey = "global-settings."+key;
        return fullKey;
    }
}
