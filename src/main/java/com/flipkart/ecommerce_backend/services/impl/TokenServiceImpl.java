package com.flipkart.ecommerce_backend.services.impl;

import com.flipkart.ecommerce_backend.dtos.LoginResponse;
import com.flipkart.ecommerce_backend.dtos.RefreshTokenRequest;
import com.flipkart.ecommerce_backend.dtos.UserDto;
import com.flipkart.ecommerce_backend.security.exception.TokenException;
import com.flipkart.ecommerce_backend.handler.ErrorCode;
import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.RefreshToken;
import com.flipkart.ecommerce_backend.security.store.TokenStore;
import com.flipkart.ecommerce_backend.services.TokenService;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.access-token.expiration-ms}")
    private long accessTokenValidityMs;

    private final TokenStore tokenStore;


    @Override
    public LoginResponse generateTokens(Authentication authentication) {
        // Extract UserDetails principal from the Authentication object
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return generateTokensFromUserDetails(userDetails);
    }

    @Override
    public LoginResponse generateTokensFromUserDetails(UserDetails userDetails) {
        if (!(userDetails instanceof LocalUser localUser)) {
            log.error("UserDetails provided is not an instance of LocalUser: {}", userDetails.getClass());
            throw new IllegalArgumentException("UserDetails must be an instance of LocalUser to generate tokens properly.");
        }

        // Create the Access Token
        String accessToken = createAccessTokenInternal(localUser);

        // Create and Persist the Refresh Token via TokenStore
        RefreshToken refreshToken = tokenStore.createAndPersistRefreshToken(localUser);

        UserDto userInfo = UserDto.builder()
                .id(localUser.getId())
                .username(localUser.getUsername())
                .email(localUser.getEmail())
                .firstName(localUser.getFirstName())
                .lastName(localUser.getLastName())
                .build();

        log.info("Generated new token pair for user: {}", localUser.getUsername());
        return LoginResponse.builder()
                .message("Token pair generated successfully")
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken().toString())
                .userInfo(userInfo)
                .build();
    }

    @Override
    public String createAccessToken(LocalUser user) {
        return createAccessTokenInternal(user);
    }

    /**
     * Internal helper to generate the JWT Access Token.
     */
    private String createAccessTokenInternal(UserDetails user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidityMs);

        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        SecretKey key = getSigningKey();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("auth", authorities)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    @Override
    public String getUsernameFromToken(String token) {
        // Extracts username (subject) after validation via extractClaims
        return extractClaims(token).getSubject();
    }

    @Override
    public Claims extractClaims(String token) {
        try {
            SecretKey key = getSigningKey();
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            throw new TokenException(ErrorCode.TOKEN_INVALID, "JWT token is expired");
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
            throw new TokenException(ErrorCode.TOKEN_NOT_SUPPORTED, "JWT token is unsupported");
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token format: {}", e.getMessage());
            throw new TokenException(ErrorCode.TOKEN_INVALID, "Invalid JWT token format");
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw new TokenException(ErrorCode.TOKEN_INVALID, "Invalid JWT signature");
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty or invalid: {}", e.getMessage());
            throw new TokenException(ErrorCode.TOKEN_INVALID, "JWT claims string is empty or invalid");
        } catch (JwtException e) { // Catch broader JWT exceptions
            log.warn("JWT processing error: {}", e.getMessage());
            throw new TokenException(ErrorCode.TOKEN_INVALID, "Error processing JWT token");
        }
    }

    @Override
    public boolean validateToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            extractClaims(refreshTokenRequest.refreshToken());
            return true;
        } catch (TokenException e) {
            return false;
        }
    }

    @Override
    public Optional<RefreshToken> findRefreshToken(String token) {
        log.debug("Searching for refresh token");
        return tokenStore.findByToken(token);
    }

    @Override
    public RefreshToken verifyRefreshTokenExpiration(RefreshToken token) {
        log.debug("Verifying expiration for refresh token belonging to user: {}", token.getUser().getUsername());
        tokenStore.verifyExpiration(token);
        return token;
    }

    @Override
    public void deleteRefreshToken(String token) {
        log.info("Deleting refresh token");
        tokenStore.deleteToken(token);
    }

    /**
     * Generates the SecretKey used for signing and verifying JWTs.
     *
     * @return The SecretKey instance.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
