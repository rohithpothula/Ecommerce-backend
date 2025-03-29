package com.flipkart.ecommerce_backend.Exception;

import com.flipkart.ecommerce_backend.hander.ErrorCode;

/**
 * Thrown when attempting to create a user with a username that already exists
 */
public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException(String username) {
        super(ErrorCode.USER_ALREADY_EXISTS, String.format(ErrorCode.USER_ALREADY_EXISTS.getDefaultMessage(), username));
    }
}
