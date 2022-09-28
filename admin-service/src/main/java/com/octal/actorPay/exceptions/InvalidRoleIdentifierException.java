package com.octal.actorPay.exceptions;

public class InvalidRoleIdentifierException extends RuntimeException {
    public InvalidRoleIdentifierException(String message) {
        super(message);
    }
}