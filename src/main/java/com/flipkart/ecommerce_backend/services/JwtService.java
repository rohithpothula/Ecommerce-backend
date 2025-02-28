package com.flipkart.ecommerce_backend.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.flipkart.ecommerce_backend.models.LocalUser;
import jakarta.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${jwt.algorithm.key}")
  private String algorithmKey;

  @Value("${jwt.issuer}")
  private String jwtIssuer;

  @Value("${jwt.expiryInSeconds}")
  private int expirySeconds;

  private Algorithm algorithm;

  private static final String USERNAME_KEY = "USERNAME_KEY";

  private static final String EMAIL_VERIFICATION_KEY = "EMAIL_VERIFICATION_KEY";

  private static final String PASSWORD_RESET_VERIFICATION_KEY = "PASSWORD_RESET_VERIFICATION_KEY";

  @PostConstruct
  public void postConstruct() throws IllegalArgumentException, UnsupportedEncodingException {
    algorithm = Algorithm.HMAC256(algorithmKey);
  }

  public String generateJWT(LocalUser localUser) {
    return JWT.create()
        .withClaim(USERNAME_KEY, localUser.getUsername())
        .withIssuedAt(new Date(System.currentTimeMillis()))
        .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * expirySeconds))
        .withIssuer(jwtIssuer)
        .sign(algorithm);
  }

  public String generateEmailJwt(LocalUser localUser) {
    return JWT.create()
        .withClaim(EMAIL_VERIFICATION_KEY, localUser.getEmail())
        .withIssuedAt(new Date(System.currentTimeMillis()))
        .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * expirySeconds))
        .withIssuer(jwtIssuer)
        .sign(algorithm);
  }

  public String generatePasswordResetJwt(LocalUser localUser) {
    return JWT.create()
        .withClaim(PASSWORD_RESET_VERIFICATION_KEY, localUser.getEmail())
        .withIssuedAt(new Date(System.currentTimeMillis()))
        .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
        .withIssuer(jwtIssuer)
        .sign(algorithm);
  }

  public String getUserNameFromToken(String token) {
    DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
    return jwt.getClaim(USERNAME_KEY).asString();
  }

  public String getEmailFromToken(String token) {
    DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
    return jwt.getClaim(EMAIL_VERIFICATION_KEY).asString();
  }

  public String getPasswordResetEmailFromToken(String token) {
    DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
    return jwt.getClaim(PASSWORD_RESET_VERIFICATION_KEY).asString();
  }

  public boolean isTokenExpired(String token) {
    Date expiryDate = JWT.decode(token).getExpiresAt();
    Date currentDate = new Date(System.currentTimeMillis());
    if (currentDate.compareTo(expiryDate) > 0) {
      return true;
    } else {
      return false;
    }
  }
}
