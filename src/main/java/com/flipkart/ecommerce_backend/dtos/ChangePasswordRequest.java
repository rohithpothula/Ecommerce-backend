package com.flipkart.ecommerce_backend.dtos;

import java.util.UUID;

public record ChangePasswordRequest(
    String oldPassword,
    String newPassword,
    UUID userId
) {
    public ChangePasswordRequest {
        if (oldPassword == null || oldPassword.isBlank()) {
            throw new IllegalArgumentException("Old password cannot be null or blank");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password cannot be null or blank");
        }
    }
}
