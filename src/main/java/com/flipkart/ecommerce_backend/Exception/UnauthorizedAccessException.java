package com.flipkart.ecommerce_backend.Exception;

import com.flipkart.ecommerce_backend.hander.ErrorCode;

public class UnauthorizedAccessException  extends BusinessException {
    public UnauthorizedAccessException(String message) {
        super(ErrorCode.UNAUTHORIZED_ACCESS, message);
    }
}
