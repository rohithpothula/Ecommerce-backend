package com.flipkart.ecommerce_backend.Exception;

import com.flipkart.ecommerce_backend.hander.ErrorCode;
import java.util.List;

/**
 * Base exception for email-related business errors
 */
public class EmailException extends BusinessException {
    protected EmailException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
