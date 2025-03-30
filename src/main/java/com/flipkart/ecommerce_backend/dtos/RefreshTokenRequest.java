package com.flipkart.ecommerce_backend.dtos;

import jakarta.validation.constraints.NotBlank; // For validation

/**
 * Data Transfer Object for requesting a token refresh.
 * Contains the refresh token provided by the client.
 */
public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token cannot be blank") // Ensure the token is provided
        String refreshToken
) {}
