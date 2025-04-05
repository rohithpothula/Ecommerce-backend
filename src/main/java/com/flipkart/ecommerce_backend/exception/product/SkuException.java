package com.flipkart.ecommerce_backend.exception.product;

import com.flipkart.ecommerce_backend.exception.BusinessException;
import com.flipkart.ecommerce_backend.handler.ErrorCode;

public class SkuException extends BusinessException {
    public SkuException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
