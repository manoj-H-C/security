package com.alibou.security.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    private final String email;

    public UserNotFoundException(String email) {
        super(String.format("User not found with email: '%s'", email));
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}