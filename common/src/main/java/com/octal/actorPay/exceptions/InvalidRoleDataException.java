package com.octal.actorPay.exceptions;

public class InvalidRoleDataException extends RuntimeException {
    public InvalidRoleDataException(String message) {
        super(message);
    }
}