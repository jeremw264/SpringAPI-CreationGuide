package com.example.demo.user;

import org.springframework.http.HttpStatus;

import com.example.demo.exception.ResourceException;

public class UserResourceException extends ResourceException {

    public UserResourceException(String errorCode, String message, HttpStatus status) {
        super(errorCode, message, status);
    }
    
}
