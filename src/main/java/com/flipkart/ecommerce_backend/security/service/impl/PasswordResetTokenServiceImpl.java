package com.flipkart.ecommerce_backend.security.service.impl;

import com.flipkart.ecommerce_backend.security.exception.TokenException;
import com.flipkart.ecommerce_backend.handler.ErrorCode;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.PasswordResetToken;
import com.flipkart.ecommerce_backend.repository.PasswordResetTokenRepository;
import com.flipkart.ecommerce_backend.services.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${app.password-reset-token.expiration-ms}")
    private Long expiryDurationMs;



    @Override
    @Transactional
    public String createAndPersistPasswordResetToken(LocalUser user) {
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(expiryDurationMs);

        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        passwordResetTokenRepository.save(resetToken);
        log.info("Created password reset token for user: {}", user.getUsername());
        return token;
    }

    @Override
    @Transactional
    public LocalUser validatePasswordResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenException(ErrorCode.TOKEN_INVALID,"Invalid password reset token"));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            log.warn("Attempted to use expired password reset token: {}", token);
            throw new TokenException(ErrorCode.TOKEN_EXPIRED, "Password reset token has expired");
        }

        log.info("Password reset token validated successfully for user: {}", resetToken.getUser().getUsername());

        return resetToken.getUser();
    }

    @Override
    @Transactional
    public void deletePasswordResetToken(String token) {
        passwordResetTokenRepository.deleteByToken(token);
        log.info("Deleted password reset token: {}", token);
    }

    @Override
    @Transactional
    public int deleteExpiredTokens() {
        int deletedCount = passwordResetTokenRepository.deleteByExpiryDateBefore(Instant.now());
        if (deletedCount > 0) {
            log.info("Deleted {} expired password reset tokens.", deletedCount);
        }
        return deletedCount;
    }
}
