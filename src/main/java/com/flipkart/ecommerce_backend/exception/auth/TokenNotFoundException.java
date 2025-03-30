package com.flipkart.ecommerce_backend.exception.auth;

import com.flipkart.ecommerce_backend.hander.ErrorCode;

public class TokenNotFoundException extends TokenException {
    public TokenNotFoundException(String message) {
        super(ErrorCode.TOKEN_NOT_FOUND, message);
    }
}
