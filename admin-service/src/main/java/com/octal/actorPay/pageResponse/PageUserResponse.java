package com.octal.actorPay.pageResponse;

import com.octal.actorPay.entities.User;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class PageUserResponse<T> {

    private String message;
    private Object data;
    private Map<String, Object> pageInfo;
    private String status;
    private HttpStatus httpStatus;

    public PageUserResponse(String message, Object data, Map<String, Object> pageInfo, String status, HttpStatus httpStatus) {
        this.message = message;
        this.data = data;
        this.pageInfo = pageInfo;
        this.status = status;
        this.httpStatus = httpStatus;
    }

    public Map<String, Object> getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(Map<String, Object> pageInfo) {
        this.pageInfo = pageInfo;
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
