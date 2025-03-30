package com.flipkart.ecommerce_backend.services;

import com.flipkart.ecommerce_backend.exception.auth.TokenExpiredException;
import com.flipkart.ecommerce_backend.exception.auth.TokenNotFoundException;
import com.flipkart.ecommerce_backend.exception.user.UserVerificationTokenAlreadyVerifiedException;
import com.flipkart.ecommerce_backend.models.LocalUser;

/**
 * Service interface for managing email verification tokens and process.
 */
public interface EmailVerificationService {

    /**
     * Creates a unique email verification token for the given user,
     * persists it (potentially removing old ones), and returns the token string.
     *
     * @param user The user needing email verification.
     * @return The generated verification token string.
     */
    String createAndPersistVerificationToken(LocalUser user);

    /**
     * Verifies the user's email using the provided token.
     * Checks if the token exists, is not expired, and belongs to an unverified user.
     * If valid, marks the user as verified and deletes the token.
     *
     * @param token The verification token string from the email link.
     * @return The verified LocalUser.
     * @throws TokenNotFoundException if the token doesn't exist.
     * @throws TokenExpiredException if the token has expired.
     * @throws UserVerificationTokenAlreadyVerifiedException if the user associated with the token is already verified.
     * @throws com.flipkart.ecommerce_backend.exception.DatabaseException on persistence errors.
     */
    LocalUser verifyEmailWithToken(String token) throws TokenNotFoundException, TokenExpiredException, UserVerificationTokenAlreadyVerifiedException;

    /**
     * Deletes expired verification tokens. Intended for scheduled cleanup.
     *
     * @return The number of expired tokens deleted.
     */
    int deleteExpiredVerificationTokens();

}
