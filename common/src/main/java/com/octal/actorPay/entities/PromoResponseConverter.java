package com.octal.actorPay.entities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.dto.ApplyPromoCodeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.AttributeConverter;
import java.io.IOException;

public class PromoResponseConverter implements AttributeConverter<ApplyPromoCodeResponse, String> {

    @Autowired
    ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(PromoResponseConverter.class);

    @Override
    public String convertToDatabaseColumn(ApplyPromoCodeResponse response) {

        String responseJson = null;
        try {
            responseJson = objectMapper.writeValueAsString(response);
        } catch (final JsonProcessingException e) {
            logger.error("JSON writing error", e);
        }

        return responseJson;
    }

    @Override
    public ApplyPromoCodeResponse convertToEntityAttribute(String responseJson) {

        ApplyPromoCodeResponse response = null;
        if (responseJson != null) {
            try {
                response = objectMapper.readValue(responseJson, ApplyPromoCodeResponse.class);
            } catch (final IOException e) {
                logger.error("JSON reading error", e);
            }
        }
        return response;
    }
}
