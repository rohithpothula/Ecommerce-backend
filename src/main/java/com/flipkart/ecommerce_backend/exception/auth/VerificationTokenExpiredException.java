package com.flipkart.ecommerce_backend.exception.auth;

import com.flipkart.ecommerce_backend.handler.ErrorCode;
import com.flipkart.ecommerce_backend.security.exception.TokenException;

/**
 * Thrown when a verification token has expired and needs to be reissued
 */
public class VerificationTokenExpiredException extends TokenException {
    public VerificationTokenExpiredException(String token) {
        super(ErrorCode.TOKEN_EXPIRED, String.format("%s: %s", ErrorCode.TOKEN_EXPIRED.getDefaultMessage(), token));
    }
}
