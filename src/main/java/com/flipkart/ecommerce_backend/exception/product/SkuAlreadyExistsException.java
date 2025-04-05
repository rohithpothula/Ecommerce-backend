package com.flipkart.ecommerce_backend.exception.product;

import com.flipkart.ecommerce_backend.handler.ErrorCode;

public class SkuAlreadyExistsException extends SkuException{
    public SkuAlreadyExistsException(String sku) {
        super(ErrorCode.SKU_ALREADY_EXISTS, String.format("SKU with id '%s' already exists.", sku));
    }
}
