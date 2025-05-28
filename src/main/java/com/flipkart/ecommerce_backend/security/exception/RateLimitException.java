package com.flipkart.ecommerce_backend.security.exception;

import com.flipkart.ecommerce_backend.exception.BusinessException;
import com.flipkart.ecommerce_backend.handler.ErrorCode;

public class RateLimitException extends BusinessException{

    protected RateLimitException(ErrorCode errorCode, String message) {
	super(errorCode, message);
    }

}
