package com.octal.actorPay.exceptionHandler;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.exceptions.ErrorResponse;
import com.octal.actorPay.exceptions.NoContentFoundException;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import com.octal.actorPay.exceptions.UnauthorisedException;
import feign.FeignException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler{

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

    @ExceptionHandler({ FeignException.class })
    public void handleBadRequest(Exception ex, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        LOGS.error(ex.getMessage(), ex);
        response.sendError(HttpStatus.BAD_REQUEST.value());

    }

    @ExceptionHandler(value = NoContentFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoContentFoundException(NoContentFoundException e) {
        e.printStackTrace();
        return new ResponseEntity<>(e.getErrorResponse(), e.getErrorResponse().getResponseStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());

        // Get all errors and prepare a list of errors
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(
                        fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        body.put("message", errors.get(0));
        body.put("data", null);
        LOGS.error(errors);
        return new ResponseEntity<>(body, headers, status);

    }

}