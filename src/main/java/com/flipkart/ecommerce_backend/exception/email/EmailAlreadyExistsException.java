package com.flipkart.ecommerce_backend.exception.email;

import com.flipkart.ecommerce_backend.handler.ErrorCode;

/**
 * Thrown when attempting to register with an email address that is already in use
 */
public class EmailAlreadyExistsException extends EmailException {
    public EmailAlreadyExistsException(String email) {
        super(ErrorCode.EMAIL_ALREADY_EXISTS, String.format("Email '%s' is already registered.", email));
    }
}
