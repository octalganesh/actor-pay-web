package com.octal.actorPay.exception;

import org.springframework.http.HttpStatus;

public class NoContentFoundException extends RuntimeException {
    private ErrorResponse errorResponse;

    public NoContentFoundException(String message, String developerMessage) {
        super(message);
        errorResponse = new ErrorResponse();
        errorResponse.setDeveloperMsg(developerMessage);
        errorResponse.setErrorMsg(message);
        errorResponse.setResponseCode(HttpStatus.NO_CONTENT.value());
        errorResponse.setResponseStatus(HttpStatus.NO_CONTENT);
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }
}