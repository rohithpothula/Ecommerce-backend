package com.flipkart.ecommerce_backend.Exception;

import com.flipkart.ecommerce_backend.hander.ErrorCode;
import java.util.List;

/**
 * Base exception for token-related business errors (verification, password reset, etc.)
 */
public class TokenException extends BusinessException {
    protected TokenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
