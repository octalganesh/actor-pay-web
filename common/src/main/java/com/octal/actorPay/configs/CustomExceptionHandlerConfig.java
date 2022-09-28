package com.octal.actorPay.configs;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.WebRequest;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@SuppressWarnings("deprecation")
public class CustomExceptionHandlerConfig {

    //private static final String DEFAULT_KEY_TIMESTAMP = "timestamp";
    private static final String DEFAULT_KEY_STATUS = "status";
    private static final String DEFAULT_KEY_ERROR = "error";
    private static final String DEFAULT_KEY_ERRORS = "errors";
    private static final String DEFAULT_KEY_MESSAGE = "message";
    private static final String DEFAULT_KEY_PATH = "path";
    private static final String DEFAULT_KEY_EXCEPTION = "exception";

    public static final String KEY_STATUS = "status";
    public static final String KEY_ERROR = "error";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_ERRORS = "errors";
    String DATA = "data";
    String EXCEPTION = "exception";
    String PATH = "path";

    //

    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {

            @Override
            public Map<String ,Object> getErrorAttributes(
                    WebRequest webRequest
                    ,boolean includeStackTrace
            ) {
                Map<String ,Object> defaultMap
                        = super.getErrorAttributes( webRequest ,includeStackTrace );

                Map<String ,Object> errorAttributes = new LinkedHashMap<>();
                // Customize.
                // For eg: Only add the keys you want.
                errorAttributes.put( KEY_STATUS, String.valueOf(defaultMap.get( DEFAULT_KEY_STATUS )) );
                errorAttributes.put( KEY_MESSAGE ,defaultMap.get( DEFAULT_KEY_MESSAGE ) );
                errorAttributes.put( KEY_TIMESTAMP ,defaultMap.get( KEY_TIMESTAMP ) );
                errorAttributes.put( DATA ,null);
                errorAttributes.put( EXCEPTION ,defaultMap.get( DEFAULT_KEY_EXCEPTION ) );
                errorAttributes.put( PATH ,defaultMap.get( DEFAULT_KEY_PATH ) );
                return errorAttributes;
            }
        };
    }
}