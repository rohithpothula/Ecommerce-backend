package com.flipkart.ecommerce_backend.exception.user;

import com.flipkart.ecommerce_backend.exception.BusinessException;
import com.flipkart.ecommerce_backend.handler.ErrorCode;

/**
 * Base exception for user-related business errors
 */
public class UserException extends BusinessException {
    protected UserException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
