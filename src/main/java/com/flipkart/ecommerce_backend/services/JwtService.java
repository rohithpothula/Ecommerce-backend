package com.flipkart.ecommerce_backend.services;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;
import com.flipkart.ecommerce_backend.models.LocalUser;

import jakarta.annotation.PostConstruct;

@Service
public class JwtService {
	
	@Value("${jwt.algorithm.key}")
	private String algorithmKey;
	
	@Value("${jwt.issuer}")
	private String jwtIssuer;
	
	@Value("${jwt.expiryInSeconds}")
	private int expirySeconds;
	
	private Algorithm algorithm;
	
	@PostConstruct
	public void postConstruct() throws IllegalArgumentException, UnsupportedEncodingException {
		algorithm = Algorithm.HMAC256(algorithmKey);
	}
	
	public String generateJWT(LocalUser localUser) {
		return JWT.create().withClaim("USERNAME",localUser.getUsername())
				.withIssuedAt(new Date(System.currentTimeMillis()))
				.withExpiresAt(new Date(System.currentTimeMillis() + 1000*expirySeconds))
				.withIssuer(jwtIssuer)
				.sign(algorithm);
	}
	
	public String getUserNameFromToken(String token) {
		return (String)JWT.decode(token).getClaim("USERNAME").asString();
	}
}
