package com.flipkart.ecommerce_backend.api.models;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class SuccessResponse {
    private final String message;
    private final int status;
    private final String timestamp;
    private final Object data;

    public static SuccessResponse of(String message) {
        return builder()
                .message(message)
                .status(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    public static SuccessResponse of(String message, Object data) {
        return builder()
                .message(message)
                .status(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now().toString())
                .data(data)
                .build();
    }
}
