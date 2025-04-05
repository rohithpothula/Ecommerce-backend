package com.flipkart.ecommerce_backend.services.impl;

import com.flipkart.ecommerce_backend.security.exception.InvalidUserCredentialsException;
import com.flipkart.ecommerce_backend.exception.user.UserNotFoundException;
import com.flipkart.ecommerce_backend.dtos.ChangePasswordRequest;
import com.flipkart.ecommerce_backend.dtos.ResetPasswordRequest;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.repository.LocalUserRepository;
import com.flipkart.ecommerce_backend.security.store.TokenStore;
import com.flipkart.ecommerce_backend.services.EmailService;
import com.flipkart.ecommerce_backend.security.service.PasswordManagementService;
import com.flipkart.ecommerce_backend.services.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordManagementServiceImpl implements PasswordManagementService {
    private final LocalUserRepository localUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final TokenStore tokenStore;

    @Override
    public void changePassword(String username, ChangePasswordRequest changePasswordRequest) {
        LocalUser user = findUserByUsernameOrEmail(username);

        if (!passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
            throw new InvalidUserCredentialsException("Incorrect old password.");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
        localUserRepository.save(user);
        log.info("Password changed successfully for user: {}", username);

        tokenStore.deleteUserTokens(user);
        log.info("Existing refresh tokens invalidated for user: {}", username);
    }

    @Override
    public void initiatePasswordReset(String email) {
        LocalUser user = localUserRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        String resetToken = passwordResetTokenService.createAndPersistPasswordResetToken(user);

        try {
            emailService.sendPasswordResetMail(resetToken, user);
            log.info("Password reset email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send password reset email.", e);
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String token = resetPasswordRequest.token();

        LocalUser user = passwordResetTokenService.validatePasswordResetToken(token);

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.newPassword()));
        localUserRepository.save(user);
        log.info("Password successfully reset for user: {}", user.getUsername());

        passwordResetTokenService.deletePasswordResetToken(token);

        tokenStore.deleteUserTokens(user);
        log.info("Existing refresh tokens invalidated for user: {}", user.getUsername());

    }

    private LocalUser findUserByUsernameOrEmail(String usernameOrEmail) {
        return localUserRepository.findByUsername(usernameOrEmail)
                .or(() -> localUserRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UserNotFoundException("User not found: " + usernameOrEmail));
    }
}
