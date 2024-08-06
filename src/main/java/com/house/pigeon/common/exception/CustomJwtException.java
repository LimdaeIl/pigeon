package com.house.pigeon.common.exception;

public class CustomJwtException extends RuntimeException{
    public CustomJwtException(String message) {
        super(message);
    }
}
