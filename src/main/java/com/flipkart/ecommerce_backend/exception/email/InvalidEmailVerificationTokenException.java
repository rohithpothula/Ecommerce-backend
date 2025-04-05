package com.flipkart.ecommerce_backend.exception.email;

import com.flipkart.ecommerce_backend.security.exception.TokenException;
import com.flipkart.ecommerce_backend.handler.ErrorCode;

/**
 * Thrown when an email verification token is invalid, malformed, or does not exist
 */
public class InvalidEmailVerificationTokenException extends TokenException {
    public InvalidEmailVerificationTokenException(String token) {
        super(ErrorCode.TOKEN_INVALID, String.format("%s : %s", ErrorCode.TOKEN_INVALID.getDefaultMessage(), token));
    }
}
