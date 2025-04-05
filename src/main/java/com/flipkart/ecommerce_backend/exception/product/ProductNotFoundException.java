package com.flipkart.ecommerce_backend.exception.product;

import com.flipkart.ecommerce_backend.handler.ErrorCode;

public class ProductNotFoundException extends ProductException {
    public ProductNotFoundException(String productId) {
        super(ErrorCode.PRODUCT_NOT_FOUND, String.format("Product with id '%s' not found.", productId));
    }
}
