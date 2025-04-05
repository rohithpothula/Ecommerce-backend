package com.flipkart.ecommerce_backend.dtos;

public record ResetPasswordRequest(
    String email,
    String token,
    String newPassword,
    String confirmPassword
) {
    public boolean isValid() {
        return email != null && !email.isBlank()
            && token != null && !token.isBlank()
            && newPassword != null && !newPassword.isBlank()
            && confirmPassword != null && !confirmPassword.isBlank();
    }
}
