package com.flipkart.ecommerce_backend.Exception;

import com.flipkart.ecommerce_backend.hander.ErrorCode;
import java.util.List;

/**
 * Base exception for user-related business errors
 */
public class UserException extends BusinessException {
    protected UserException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
