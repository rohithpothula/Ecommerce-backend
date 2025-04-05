package com.flipkart.ecommerce_backend.exception.product;

import com.flipkart.ecommerce_backend.handler.ErrorCode;

public class ProductValidationException extends ProductException{
    public ProductValidationException(String productId) {
        super(ErrorCode.PRODUCT_VALIDATION_ERROR, String.format("Product with id '%s' has invalid attributes.", productId));
    }
}
