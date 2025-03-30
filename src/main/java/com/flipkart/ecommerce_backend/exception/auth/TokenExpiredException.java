package com.flipkart.ecommerce_backend.exception.auth;

import com.flipkart.ecommerce_backend.hander.ErrorCode;

public class TokenExpiredException extends TokenException {
    public TokenExpiredException(String message) {
        super(ErrorCode.TOKEN_EXPIRED, message);
    }
}
