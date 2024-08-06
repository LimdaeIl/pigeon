package com.house.pigeon.common.exception;

public class CustomExpiredTokenException extends RuntimeException {
    public CustomExpiredTokenException(String message) {
        super(message);
    }
}
