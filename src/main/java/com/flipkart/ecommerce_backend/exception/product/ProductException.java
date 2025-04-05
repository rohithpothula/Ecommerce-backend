package com.flipkart.ecommerce_backend.exception.product;

import com.flipkart.ecommerce_backend.exception.BusinessException;
import com.flipkart.ecommerce_backend.handler.ErrorCode;

/**
 * Base exception for product-related business errors
 */
public class ProductException extends BusinessException {
    public ProductException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
