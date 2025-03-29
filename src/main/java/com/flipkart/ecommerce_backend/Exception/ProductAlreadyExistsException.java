package com.flipkart.ecommerce_backend.Exception;

import com.flipkart.ecommerce_backend.hander.ErrorCode;

public class ProductAlreadyExistsException extends ProductException{
    public ProductAlreadyExistsException(String productId) {
        super(ErrorCode.PRODUCT_ALREADY_EXISTS, String.format("Product with id '%s' already exists.", productId));
    }
}
