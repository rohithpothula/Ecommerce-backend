package com.flipkart.ecommerce_backend.exception.user;

import com.flipkart.ecommerce_backend.handler.ErrorCode;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(String userId) {
        super(ErrorCode.USER_NOT_FOUND,
                String.format(ErrorCode.USER_NOT_FOUND.getDefaultMessage()," with ID: %s", userId));
    }}
