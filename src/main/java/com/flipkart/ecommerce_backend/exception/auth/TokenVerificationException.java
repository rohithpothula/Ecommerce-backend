package com.flipkart.ecommerce_backend.exception.auth;

import com.flipkart.ecommerce_backend.handler.ErrorCode;
import com.flipkart.ecommerce_backend.security.exception.TokenException;

public class TokenVerificationException extends TokenException {
    public TokenVerificationException(String message) {
        super(ErrorCode.TOKEN_INVALID, message);
    }
}
