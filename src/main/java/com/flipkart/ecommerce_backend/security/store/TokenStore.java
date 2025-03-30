package com.flipkart.ecommerce_backend.security.store;

import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.RefreshToken;

import java.util.Optional;

public interface TokenStore {
    RefreshToken createAndPersistRefreshToken(LocalUser user);
    Optional<RefreshToken> findByToken(String token);
    void verifyExpiration(RefreshToken token);
    void deleteToken(String token);
    void deleteUserTokens(LocalUser user);
}
