package com.octal.actorPay.exceptions;

public class RestClientException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int httpStatus;

    private String message;

    public RestClientException() {
        super();
    }

    public RestClientException(String message) {
        super(message);
    }

    public RestClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestClientException(int httpStatus, String message) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
