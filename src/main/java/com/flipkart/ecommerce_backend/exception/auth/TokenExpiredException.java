package com.flipkart.ecommerce_backend.exception.auth;

import com.flipkart.ecommerce_backend.handler.ErrorCode;
import com.flipkart.ecommerce_backend.security.exception.TokenException;

public class TokenExpiredException extends TokenException {
    public TokenExpiredException(String message) {
        super(ErrorCode.TOKEN_EXPIRED, message);
    }
}
