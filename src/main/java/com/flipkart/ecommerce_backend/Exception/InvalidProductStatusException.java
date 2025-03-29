package com.flipkart.ecommerce_backend.Exception;

import com.flipkart.ecommerce_backend.hander.ErrorCode;

public class InvalidProductStatusException extends ProductException{
    public InvalidProductStatusException(String productId) {
        super(ErrorCode.INVALID_PRODUCT_STATUS, String.format("Product with id '%s' has invalid status.", productId));
    }
}
