package com.octal.actorPay.exceptionHandler;

import com.octal.actorPay.dto.ApiResponse;
import com.octal.actorPay.exceptions.ObjectNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException arguments, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        BindingResult bindingResult = arguments.getBindingResult();
        List<String> validationMessages = new ArrayList<>();
        List<ObjectError> objectErrors = bindingResult.getAllErrors();
        for (ObjectError objectError : objectErrors) {
            String defaultMessage = objectError.getDefaultMessage();
            validationMessages.add(defaultMessage);
        }
        return new ResponseEntity<>(new ApiResponse("Validation Error: ",null,
                String.valueOf(HttpStatus.BAD_REQUEST.value()),HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<ApiResponse> handleCustomException(Exception ex, WebRequest request) throws Exception {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage(),null,
                String.valueOf(HttpStatus.BAD_REQUEST.value()),HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public final ResponseEntity<ApiResponse> handleObjectNotFoundException(Exception ex, WebRequest request) throws Exception {
        return new ResponseEntity<>(new ApiResponse(ex.getMessage(),null,
                String.valueOf(HttpStatus.BAD_REQUEST.value()),HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
}
