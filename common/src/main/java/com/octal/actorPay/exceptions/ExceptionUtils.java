package com.octal.actorPay.exceptions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octal.actorPay.constants.CommonConstant;
import com.octal.actorPay.dto.ApiResponse;
import feign.FeignException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collection;

public class ExceptionUtils {

    public static void duplicateCheckHandleException(DataIntegrityViolationException e, String id) {
        if (e != null) {
            if (e.getMostSpecificCause() != null) {
                String message = e.getMostSpecificCause().getMessage();
                System.out.println("Error message " + message);
                if(message.contains(CommonConstant.DUPLICATE_ENTRY)) {
                    throw new RuntimeException(String.format("The given Entity Attribute(s) are already exist - %s ",id));
                }else{
                    throw new RuntimeException(String.format("Unable to update Entity - %s ",id));
                }
            } else {
                throw new RuntimeException("Unable to update Entity");
            }
        } else {
            throw new RuntimeException("Unable to update Entity");
        }
    }

    public static String duplicateCheckHandleException(DataIntegrityViolationException e) {
        String defaultError = "Unknown Error ";
        if (e != null) {
            if (e.getMostSpecificCause() != null) {
                String message = e.getMostSpecificCause().getMessage();
                System.out.println("Error message " + message);
                if(message.contains(CommonConstant.DUPLICATE_ENTRY)) {
                    return message;
                }else{
                    return defaultError;
                }
            } else {
                return defaultError;
            }
        } else {
            return defaultError;
        }
    }

    public static ApiResponse parseFeignExceptionMessage(FeignException fe){

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            String content = fe.contentUTF8();
            Collection<ApiResponse> apiResponse = objectMapper.readValue(
                    content, new TypeReference<Collection<ApiResponse>>() {
                    });
            System.out.println(apiResponse.stream().findFirst().get());
            System.out.println(fe.getMessage());
            return apiResponse.stream().findFirst().get();
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
