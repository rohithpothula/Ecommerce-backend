package com.flipkart.ecommerce_backend.dtos;

/**
 * DTO containing only the newly generated JWT access token. Used specifically for the token refresh
 * response if the refresh token itself is not returned. NOTE: Returning both tokens via
 * AuthResponse is generally preferred.
 */
public record RefreshTokenResponse(String accessToken) {}
