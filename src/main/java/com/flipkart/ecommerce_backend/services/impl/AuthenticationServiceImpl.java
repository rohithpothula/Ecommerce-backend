package com.flipkart.ecommerce_backend.services.impl;

import com.flipkart.ecommerce_backend.dtos.*;
import com.flipkart.ecommerce_backend.security.exception.TokenException;
import com.flipkart.ecommerce_backend.handler.ErrorCode;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.RefreshToken;
import com.flipkart.ecommerce_backend.services.AuthenticationService;
import com.flipkart.ecommerce_backend.services.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.username());
        Authentication authentication;

        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (AuthenticationException e) {
            log.warn("Login failed for user {}: {}", request.username(), e.getMessage());
            throw e; // Let ControllerAdvice handle specific AuthenticationExceptions
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof LocalUser user)) {
            log.error("Authentication principal is not an instance of LocalUser: {}", principal.getClass());
            throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
        }
        LoginResponse tokenResponse = tokenService.generateTokens(authentication);
        log.info("Login successful and tokens generated for user: {}", user.getUsername());
        return tokenResponse;
    }

    @Override
    public void logout(RefreshTokenRequest refreshTokenRequest) {
        String token = refreshTokenRequest.refreshToken();

        if (!StringUtils.hasText(token)) {
            log.warn("Logout attempt with missing refresh token.");
            return;
        }
        log.info("Processing logout: Invalidating refresh token");
        try {
            tokenService.deleteRefreshToken(token);
        } catch (Exception e) {
            log.error("Error occurred while deleting refresh token during logout: {}", e.getMessage());
            throw new RuntimeException("Logout failed", e);
        } finally {
            // Always clear the context for the current thread, though its impact
            // is limited in stateless JWT scenarios, it's good practice.
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.refreshToken();
        log.info("Processing refresh token request");

        return tokenService.findRefreshToken(requestRefreshToken)
                .map(tokenService::verifyRefreshTokenExpiration) // Throws TokenException if expired/invalid
                .map(RefreshToken::getUser)
                .map(user -> {
                    // Generate new pair of tokens for the user using UserDetails
                    LoginResponse newTokens = tokenService.generateTokensFromUserDetails(user);
                    // Invalidate the *old* refresh token AFTER generating new ones
                    tokenService.deleteRefreshToken(requestRefreshToken);
                    log.info("Tokens refreshed successfully for user: {}", user.getUsername());
                    return newTokens;
                })
                .orElseThrow(() -> {
                    log.warn("Refresh token not found or invalid during refresh attempt.");
                    return new TokenException(ErrorCode.TOKEN_INVALID,"Refresh token not found or invalid");
                });
    }

    @Override
    public boolean validateToken(String token) {
        try {
            tokenService.extractClaims(token);
            return true;
        } catch (TokenException e) {
            log.warn("Token validation failed: {}", e.getMessage());
        }
        return false;
    }
}
