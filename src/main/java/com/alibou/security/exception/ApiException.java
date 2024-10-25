package com.alibou.security.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException{
    //    we throw this exception whenever we write some business logic or validate request parameters.

    private final HttpStatus status;
    private final String message;
    public ApiException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
