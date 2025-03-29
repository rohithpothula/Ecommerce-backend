package com.flipkart.ecommerce_backend.Exception;

import com.flipkart.ecommerce_backend.hander.ErrorCode;

public class TokenVerificationException extends TokenException {
    public TokenVerificationException(String message) {
        super(ErrorCode.TOKEN_INVALID, message);
    }
}
