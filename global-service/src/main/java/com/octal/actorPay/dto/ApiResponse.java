package com.octal.actorPay.dto;

import org.springframework.http.HttpStatus;

public class ApiResponse {

    private String message;
    private Object data;
    private String status;
    private HttpStatus httpStatus;

    public ApiResponse() {
    }

    public ApiResponse(String message, Object data, String status, HttpStatus httpStatus) {
        this.message = message;
        this.data = data;
        this.status = status;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}