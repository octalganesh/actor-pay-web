package com.octal.actorPay.exceptions;

public class InvalidPermissionDataException extends RuntimeException {
    public InvalidPermissionDataException(String message) {
        super(message);
    }
}