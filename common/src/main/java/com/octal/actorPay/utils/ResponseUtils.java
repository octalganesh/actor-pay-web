package com.octal.actorPay.utils;

import com.octal.actorPay.dto.ApiResponse;
import org.springframework.http.HttpStatus;

import java.util.Random;

public class ResponseUtils {

    public static ApiResponse ActorPayResponse(String message, Object o, HttpStatus httpStatus) {
       return new ApiResponse(message,o,String.valueOf(httpStatus.value()),httpStatus);
    }
}