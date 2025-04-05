package com.flipkart.ecommerce_backend.exception.auth;

import com.flipkart.ecommerce_backend.handler.ErrorCode;
import com.flipkart.ecommerce_backend.security.exception.TokenException;

public class TokenNotFoundException extends TokenException {
    public TokenNotFoundException(String message) {
        super(ErrorCode.TOKEN_NOT_FOUND, message);
    }
}
