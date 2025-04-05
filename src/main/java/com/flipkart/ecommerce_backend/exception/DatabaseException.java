package com.flipkart.ecommerce_backend.exception;

import com.flipkart.ecommerce_backend.handler.ErrorCode;

import java.util.Collections;

public class DatabaseException extends BusinessException {
    public DatabaseException(String message) {
        super(ErrorCode.DATABASE_ERROR, message);
    }

    public DatabaseException(String message, String details) {
        super(ErrorCode.DATABASE_ERROR, message, Collections.singletonList(details));
    }

    public DatabaseException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public DatabaseException(ErrorCode errorCode, String message, String details) {
        super(errorCode, message, Collections.singletonList(details));
    }
}
