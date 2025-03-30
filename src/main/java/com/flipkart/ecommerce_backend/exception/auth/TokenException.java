package com.flipkart.ecommerce_backend.exception.auth;

import com.flipkart.ecommerce_backend.exception.BusinessException;
import com.flipkart.ecommerce_backend.hander.ErrorCode;

/**
 * Base exception for token-related business errors (verification, password reset, etc.)
 */
public class TokenException extends BusinessException {
    public TokenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
