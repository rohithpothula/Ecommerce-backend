package com.flipkart.ecommerce_backend.exception.role;

import com.flipkart.ecommerce_backend.exception.BusinessException;
import com.flipkart.ecommerce_backend.handler.ErrorCode;

/**
 * Base exception for role-related business errors
 */
public class RoleException extends BusinessException {
    protected RoleException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
