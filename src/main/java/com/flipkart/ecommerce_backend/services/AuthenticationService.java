package com.flipkart.ecommerce_backend.services;

import com.flipkart.ecommerce_backend.dtos.LoginRequest;
import com.flipkart.ecommerce_backend.dtos.LoginResponse;
import com.flipkart.ecommerce_backend.dtos.RefreshTokenRequest;

public interface AuthenticationService {
  LoginResponse login(LoginRequest request);

  void logout(RefreshTokenRequest request);

  LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

  boolean validateToken(String token);
}
