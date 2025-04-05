package com.flipkart.ecommerce_backend.services;

import com.flipkart.ecommerce_backend.dtos.LoginResponse;
import com.flipkart.ecommerce_backend.dtos.RefreshTokenRequest;
import com.flipkart.ecommerce_backend.security.exception.TokenException;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.RefreshToken;
import io.jsonwebtoken.Claims; // Import from your JWT library (e.g., jjwt)
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * Service interface for handling JWT (Access Tokens) and Refresh Tokens.
 * Defines operations for creating, validating, extracting information from,
 * and managing the lifecycle of tokens.
 */
public interface TokenService {

    /**
     * Generates a pair of tokens (access and refresh) for an authenticated user.
     * Typically called after successful username/password authentication.
     *
     * @param authentication The Spring Security Authentication object containing principal details.
     * @return An AuthResponse DTO containing the new access and refresh tokens.
     */
    LoginResponse generateTokens(Authentication authentication);

    /**
     * Generates a pair of tokens (access and refresh) directly from UserDetails.
     * Useful during the refresh token flow.
     *
     * @param userDetails The UserDetails of the user for whom to generate tokens.
     * @return An AuthResponse DTO containing the new access and refresh tokens.
     */
    LoginResponse generateTokensFromUserDetails(UserDetails userDetails);

    /**
     * Creates only the JWT Access Token for a given user.
     * This might be used internally or for specific flows if needed.
     *
     * @param user The LocalUser entity for whom to create the token.
     * @return The generated JWT access token string.
     */
    String createAccessToken(LocalUser user);

    /**
     * Extracts the username (subject) from a given JWT access token.
     * Validates the token internally before extracting.
     *
     * @param token The JWT access token string.
     * @return The username stored in the token's subject claim.
     * @throws TokenException if the token is invalid or expired.
     */
    String getUsernameFromToken(String token);

    /**
     * Extracts all claims from a given JWT access token.
     * Performs validation (signature, expiration) before extracting.
     *
     * @param token The JWT access token string.
     * @return The Claims object containing the token payload.
     * @throws TokenException if the token is invalid (signature, format, expired, unsupported, empty claims).
     */
    Claims extractClaims(String token);

    /**
     * Validates the structure, signature, and expiration of a JWT access token.
     *
     * @param refreshTokenRequest The JWT access token string to validate.
     * @return true if the token is valid, false otherwise.
     */
    boolean validateToken(RefreshTokenRequest refreshTokenRequest);

    /**
     * Finds a persisted Refresh Token by its token string.
     *
     * @param token The refresh token string.
     * @return An Optional containing the RefreshToken entity if found, otherwise empty.
     */
    Optional<RefreshToken> findRefreshToken(String token);

    /**
     * Verifies that the given Refresh Token has not expired.
     * If it has expired, it should be removed from the store, and an exception thrown.
     *
     * @param token The RefreshToken entity to verify.
     * @return The same RefreshToken entity if it's valid and not expired.
     * @throws TokenException if the token is expired.
     */
    RefreshToken verifyRefreshTokenExpiration(RefreshToken token);

    /**
     * Deletes a Refresh Token from the persistent store using its token string.
     * Used during logout or when a refresh token is successfully used to get new tokens.
     *
     * @param token The refresh token string to delete.
     */
    void deleteRefreshToken(String token);

}
