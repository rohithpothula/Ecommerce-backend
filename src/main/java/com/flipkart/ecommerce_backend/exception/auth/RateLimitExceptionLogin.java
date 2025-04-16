package com.flipkart.ecommerce_backend.exception.auth;

import com.flipkart.ecommerce_backend.handler.ErrorCode;
import com.flipkart.ecommerce_backend.security.exception.RateLimitException;

public class RateLimitExceptionLogin extends RateLimitException {

    public RateLimitExceptionLogin(ErrorCode errorCode, String message) {
	super(ErrorCode.RATE_LIMIT_ERROR_LOGIN, message);
    }

}
