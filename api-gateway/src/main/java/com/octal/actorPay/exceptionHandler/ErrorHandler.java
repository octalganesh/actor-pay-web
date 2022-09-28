package com.octal.actorPay.exceptionHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGS = LogManager.getLogger(ErrorHandler.class);

    /**
     * this method handles authentication related exceptions and shows error message
     * in the format of json
     *
     * @param ex       - RuntimeException class object
     * @param request  - servlet request object
     * @param response - servlet response object
     * @throws IOException - throws io exception
     */
    @ExceptionHandler({ UnauthorisedException.class })
    public void handleUnauthorizedException(Exception ex, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        LOGS.error(ex.getMessage(), ex);
        response.sendError(HttpStatus.UNAUTHORIZED.value());

    }


    // @Validate For Validating Path Variables and Request Parameters
    @ExceptionHandler(ConstraintViolationException.class)
    public void constraintViolationException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

}