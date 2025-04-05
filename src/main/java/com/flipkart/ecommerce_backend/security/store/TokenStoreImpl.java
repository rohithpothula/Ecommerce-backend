package com.flipkart.ecommerce_backend.security.store;

import com.flipkart.ecommerce_backend.security.exception.TokenException;
import com.flipkart.ecommerce_backend.handler.ErrorCode;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.RefreshToken;
import com.flipkart.ecommerce_backend.repository.LocalUserRepository;
import com.flipkart.ecommerce_backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenStoreImpl implements  TokenStore {
    private final RefreshTokenRepository refreshTokenRepository;
    private final LocalUserRepository localUserRepository; // Needed if LocalUser detached

    @Value("${app.jwt.refresh-token.expiration-ms}")
    private Long refreshTokenDurationMs;

    @Override
    @Transactional
    public RefreshToken createAndPersistRefreshToken(LocalUser user) {
        // Ensure user is managed if potentially detached
        LocalUser managedUser = user.getId() == null ? user :
                localUserRepository.findById(user.getId())
                        .orElseThrow(() -> new RuntimeException("User not found for token creation"));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(managedUser);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString()); // Simple opaque token
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);

    }

    @Override
    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token); // Clean up expired token
            throw new TokenException(ErrorCode.TOKEN_EXPIRED, "Refresh token was expired. Please make a new sign-in request");
        }
    }

    @Override
    @Transactional
    public void deleteToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void deleteUserTokens(LocalUser user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
