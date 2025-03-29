package com.flipkart.ecommerce_backend.Exception;

import com.flipkart.ecommerce_backend.hander.ErrorCode;

public class ProductValidationException extends ProductException{
    public ProductValidationException(String productId) {
        super(ErrorCode.PRODUCT_VALIDATION_ERROR, String.format("Product with id '%s' has invalid attributes.", productId));
    }
}
