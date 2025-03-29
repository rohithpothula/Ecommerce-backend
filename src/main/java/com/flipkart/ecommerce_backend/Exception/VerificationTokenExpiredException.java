package com.flipkart.ecommerce_backend.Exception;

import com.flipkart.ecommerce_backend.hander.ErrorCode;

/**
 * Thrown when a verification token has expired and needs to be reissued
 */
public class VerificationTokenExpiredException extends TokenException {
    public VerificationTokenExpiredException(String token) {
        super(ErrorCode.TOKEN_EXPIRED, String.format("%s: %s", ErrorCode.TOKEN_EXPIRED.getDefaultMessage(), token));
    }
}
