package com.flipkart.ecommerce_backend.exception;

import com.flipkart.ecommerce_backend.handler.ErrorCode;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Base exception class for all custom exceptions in the application.
 * Provides common functionality for error codes, details, and timestamps.
 */
@Getter
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final List<String> details;
    private final LocalDateTime timestamp;

    protected BaseException(ErrorCode errorCode, String message) {
        this(message, errorCode, Collections.emptyList());
    }

    protected BaseException(String message, ErrorCode errorCode, List<String> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details != null ? details : Collections.emptyList();
        this.timestamp = LocalDateTime.now();
    }

    protected BaseException(String message, ErrorCode errorCode, String details) {
        this(message, errorCode, details != null ? Collections.singletonList(details) : Collections.emptyList());
    }
}
