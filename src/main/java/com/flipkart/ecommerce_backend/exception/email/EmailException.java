package com.flipkart.ecommerce_backend.exception.email;

import com.flipkart.ecommerce_backend.exception.BusinessException;
import com.flipkart.ecommerce_backend.handler.ErrorCode;

/**
 * Base exception for email-related business errors
 */
public class EmailException extends BusinessException {
    protected EmailException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
