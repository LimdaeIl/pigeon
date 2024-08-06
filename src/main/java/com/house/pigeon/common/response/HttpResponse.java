package com.house.pigeon.common.response;

public record HttpResponse<T>(Integer code, String message, T data) {
}
