package com.flipkart.ecommerce_backend.dtos;

public record TokenResponse(
    String accessToken, String refreshToken, long expiresIn, String tokenType) {
  public static TokenResponse from(TokenPair tokenPair, long expiresIn) {
    return new TokenResponse(
        tokenPair.accessToken(), tokenPair.refreshToken(), expiresIn, "Bearer");
  }
}
