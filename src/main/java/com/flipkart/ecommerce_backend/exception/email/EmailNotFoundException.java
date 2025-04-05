package com.flipkart.ecommerce_backend.exception.email;

import com.flipkart.ecommerce_backend.handler.ErrorCode;

/**
 * Thrown when an email address cannot be found
 */
public class EmailNotFoundException extends EmailException {
    public EmailNotFoundException(String email) {
        super(ErrorCode.EMAIL_NOT_FOUND, String.format("%s : %s", ErrorCode.EMAIL_NOT_FOUND.getDefaultMessage(), email));
    }
}
