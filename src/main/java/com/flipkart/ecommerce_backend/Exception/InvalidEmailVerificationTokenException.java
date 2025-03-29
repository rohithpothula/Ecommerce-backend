package com.flipkart.ecommerce_backend.Exception;

import com.flipkart.ecommerce_backend.hander.ErrorCode;

/**
 * Thrown when an email verification token is invalid, malformed, or does not exist
 */
public class InvalidEmailVerificationTokenException extends TokenException {
    public InvalidEmailVerificationTokenException(String token) {
        super(ErrorCode.TOKEN_INVALID, String.format("%s : %s", ErrorCode.TOKEN_INVALID.getDefaultMessage(), token));
    }
}
