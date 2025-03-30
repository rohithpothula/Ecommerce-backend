package com.flipkart.ecommerce_backend.services.impl;


import com.flipkart.ecommerce_backend.exception.DatabaseException;
import com.flipkart.ecommerce_backend.exception.auth.TokenExpiredException;
import com.flipkart.ecommerce_backend.exception.auth.TokenNotFoundException;
import com.flipkart.ecommerce_backend.exception.user.UserVerificationTokenAlreadyVerifiedException;
import com.flipkart.ecommerce_backend.models.EmailVerificationToken;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.repository.EmailVerificationTokenRepository;
import com.flipkart.ecommerce_backend.repository.LocalUserRepository;
import com.flipkart.ecommerce_backend.services.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationTokenRepository verificationTokenRepository;
    private final LocalUserRepository userRepository;

    @Value("${app.email.verification.token.expiration-ms}")
    private Long verificationTokenExpiryMs;

    @Override
    @Transactional
    public String createAndPersistVerificationToken(LocalUser user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User must be persisted before creating a verification token.");
        }
        verificationTokenRepository.findByUser_Id(user.getId())
                .ifPresent(existingToken -> {
                    log.debug("Deleting existing verification token for user {}", user.getId());
                    verificationTokenRepository.delete(existingToken);
                });

        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(verificationTokenExpiryMs);

        EmailVerificationToken verificationToken = new EmailVerificationToken(token, user, expiryDate);

        try {
            verificationTokenRepository.save(verificationToken);
            log.info("Created email verification token for user: {}", user.getUsername());
            return token;
        } catch (DataIntegrityViolationException e) {
            log.error("Database error while saving email verification token for user {}: {}", user.getUsername(), e.getMessage());
            throw new DatabaseException("Failed to create verification token.", e.getMessage());
        }
    }

    @Override
    @Transactional
    public LocalUser verifyEmailWithToken(String token) throws TokenNotFoundException, TokenExpiredException, UserVerificationTokenAlreadyVerifiedException {
        log.debug("Attempting to verify email with token");

        EmailVerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Email verification failed: Token not found.");
                    return new TokenNotFoundException("Invalid verification token.");
                });

        if (verificationToken.isExpired()) {
            log.warn("Email verification failed: Token expired for user {}", verificationToken.getUser().getUsername());
            verificationTokenRepository.delete(verificationToken);
            throw new TokenExpiredException("Verification token has expired.");
        }

        LocalUser user = verificationToken.getUser();
        if (user == null) {
            log.error("Verification token {} has no associated user!", verificationToken.getId());
            verificationTokenRepository.delete(verificationToken);
            throw new TokenNotFoundException("Invalid verification token (user link missing).");
        }

        if (user.isEmailVerified()) {
            log.warn("Email verification attempt for already verified user: {}", user.getUsername());
            verificationTokenRepository.delete(verificationToken);
            throw new UserVerificationTokenAlreadyVerifiedException("User email (" + user.getEmail() + ") is already verified.");
        }

        user.setEmailVerified(true);
        user.setEnabled(true);
        try {
            userRepository.save(user);
            log.info("Email verified successfully for user: {}", user.getUsername());
        } catch (DataIntegrityViolationException e) {
            log.error("Database error while marking user {} as verified: {}", user.getUsername(), e.getMessage());
            throw new DatabaseException("Failed to update user verification status.", e.getMessage());
        }

        verificationTokenRepository.delete(verificationToken);
        log.debug("Deleted used verification token for user {}", user.getUsername());

        return user;
    }

    @Override
    @Transactional
    public int deleteExpiredVerificationTokens() {
        int deletedCount = verificationTokenRepository.deleteAllByExpiryDateBefore(Instant.now());
        if (deletedCount > 0) {
            log.info("Deleted {} expired email verification tokens.", deletedCount);
        }
        return deletedCount;
    }
}
