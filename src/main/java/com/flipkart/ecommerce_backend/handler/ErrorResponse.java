package com.flipkart.ecommerce_backend.handler;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {
    private final String code;
    private final String message;
    private final int status;
    private final String timestamp;
    private final List<String> details;

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return builder()
                .code(errorCode.getCode())
                .message(message)
                .status(errorCode.getHttpStatus().value())
                .timestamp(LocalDateTime.now().toString())
                .details(Collections.emptyList())
                .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String message, List<String> details) {
        return builder()
                .code(errorCode.getCode())
                .message(message)
                .status(errorCode.getHttpStatus().value())
                .timestamp(LocalDateTime.now().toString())
                .details(details)
                .build();
    }
}

