package com.flipkart.ecommerce_backend.services;


import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.security.exception.TokenException;


public interface PasswordResetTokenService {

    /**
     * Creates a unique password reset token for the given user, persists it,
     * and returns the token string.
     *
     * @param user The user requesting the password reset.
     * @return The generated password reset token string.
     */
    String createAndPersistPasswordResetToken(LocalUser user);

    /**
     * Validates the given password reset token. Checks for existence and expiration.
     *
     * @param token The token string to validate.
     * @return The LocalUser associated with the valid token.
     * @throws TokenException if the token is invalid or expired.
     */
    LocalUser validatePasswordResetToken(String token);

    /**
     * Deletes the specified password reset token from the store.
     * Typically called after a successful password reset.
     *
     * @param token The token string to delete.
     */
    void deletePasswordResetToken(String token);

    /**
     * Deletes all expired password reset tokens. Intended for scheduled cleanup.
     *
     * @return The number of expired tokens deleted.
     */
    int deleteExpiredTokens();
}
