package com.flipkart.ecommerce_backend.services;

import com.flipkart.ecommerce_backend.dtos.ChangePasswordRequest;
import com.flipkart.ecommerce_backend.dtos.ResetPasswordRequest;

public interface PasswordManagementService {
    public void changePassword(String username, ChangePasswordRequest changePasswordRequest);
    public void initiatePasswordReset(String email);
    public void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
