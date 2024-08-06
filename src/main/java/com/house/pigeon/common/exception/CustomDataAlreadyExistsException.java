package com.house.pigeon.common.exception;

public class CustomDataAlreadyExistsException extends RuntimeException {
    public CustomDataAlreadyExistsException(String message) {
        super(message);
    }
}
