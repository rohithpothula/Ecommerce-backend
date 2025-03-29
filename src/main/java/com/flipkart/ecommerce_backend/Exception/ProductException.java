package com.flipkart.ecommerce_backend.Exception;

import com.flipkart.ecommerce_backend.hander.ErrorCode;

/**
 * Base exception for product-related business errors
 */
public class ProductException extends BusinessException{
    public ProductException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
