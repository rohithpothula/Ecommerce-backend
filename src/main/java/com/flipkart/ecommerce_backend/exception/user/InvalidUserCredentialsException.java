package com.flipkart.ecommerce_backend.exception.user;

import com.flipkart.ecommerce_backend.hander.ErrorCode;

/**
 * Thrown when login credentials are invalid
 */
public class InvalidUserCredentialsException extends UserException {
    public InvalidUserCredentialsException() {
        super(ErrorCode.INVALID_CREDENTIALS, "Invalid username or password provided.");
    }

    public InvalidUserCredentialsException(String message) {
        super(ErrorCode.INVALID_CREDENTIALS, message);
    }
}
