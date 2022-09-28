package com.octal.actorPay.exceptionHandler;

import org.springframework.http.HttpStatus;

public class UnauthorisedException extends RuntimeException {
    private ErrorResponse errorResponse;

    public UnauthorisedException(String message, String developerMessage) {
        super(message);
        errorResponse = new ErrorResponse();
        errorResponse.setDeveloperMsg(developerMessage);
        errorResponse.setErrorMsg(message);
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED);
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
    public void setErrorResponse(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }
}