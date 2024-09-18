package com.flipkart.ecommerce_backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class EncryptionService {
	
	@Value("${encryption.salt.rounds}")
	private int saltRounds;
	
	private String salt;
	
	@PostConstruct
	public void postconstruct() {
		salt = BCrypt.gensalt(saltRounds);
	}
	
	public String encryptPassword(String password) {
		return BCrypt.hashpw(password, this.salt);
	}
	
	public boolean verifyPassword(String password,String hash) {
		return BCrypt.checkpw(password,hash);
	}

}
