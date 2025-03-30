package com.flipkart.ecommerce_backend.dtos;

import jakarta.validation.constraints.NotNull;

public record TokenPair(
        @NotNull String accessToken,
        @NotNull String refreshToken
) {
    public static TokenPair of(String accessToken, String refreshToken) {
        return new TokenPair(accessToken, refreshToken);
    }
}
