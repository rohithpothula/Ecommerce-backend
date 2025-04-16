package com.flipkart.ecommerce_backend.security.config;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;

import com.flipkart.ecommerce_backend.controllers.auth.AuthenticationController;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RateLimitConfig {
    
    private final Map<String,Bucket> loginBuckets = new ConcurrentHashMap<String,Bucket>();
    private final Map<String,Bucket> resetPasswordBuckets = new ConcurrentHashMap<String, Bucket>();
    private final Map<String,Instant> lastAccessTime = new ConcurrentHashMap<String, Instant>();
    
    @Value("${rate-limit.login.attempts}")
    private int loginAttempts = 5;
    
    @Value("${rate-limit.login.duration-Minutes}")
    private int loginDurationMinutes = 1;
    
    @Value("${rate-limit.reset.password.attempts}")
    private int resetPasswordAttempts=3;
    
    @Value("${rate-limit.reset.duration-Hours}")
    private int resetPasswordDuration = 1;
    
    public Bucket resolveBucketForLogin(String key) {
	lastAccessTime.put(key, Instant.now());
	return loginBuckets.computeIfAbsent(key, k-> createLoginBucket());
    }
    
    public Bucket resolveBucketForResetPassword(String key) {
	lastAccessTime.put(key, Instant.now());
	return resetPasswordBuckets.computeIfAbsent(key, k->createResetPasswordBucket());
    }
    
    private Bucket createLoginBucket() {
	Bandwidth limit = Bandwidth.classic(1, Refill.intervally(1, Duration.ofMinutes(1)));
	return Bucket.builder().addLimit(limit).build();
    }
    
    private Bucket createResetPasswordBucket() {
	Bandwidth limit = Bandwidth.classic(3, Refill.intervally(3, Duration.ofHours(24)));
	return Bucket.builder().addLimit(limit).build();
    }
    
    
    @PreAuthorize("hasRole('ADMIN')")
    public void cleanBucketOfLogin(String key) {
	lastAccessTime.remove(key);
	loginBuckets.remove(key);
	log.info("Rate Limit key {} is cleared by Admin",key);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public void cleanBucketofResetPassword(String key) {
	lastAccessTime.remove(key);
	resetPasswordBuckets.remove(key);
	log.info("Rate Limit key {} is cleared by Admin",key);
    }
    
    @Scheduled(fixedDelay = 3600000)
    public void cleanUpStaleBuckets() {
	Instant stateThreshold = Instant.now().minus(Duration.ofHours(24));
	
	int beforeCleanUpSize = lastAccessTime.size();
	lastAccessTime.entrySet().removeIf(entry->{
	    if(entry.getValue().isBefore(stateThreshold)) {
		loginBuckets.remove(entry.getKey());
		resetPasswordBuckets.remove(entry.getKey());
		return true;
	    }
	    return false;
	});
	
	int afterCleanUpSize = beforeCleanUpSize - lastAccessTime.size();
	log.info("CleanUp Completed . Removed {} no of stale Buckets",afterCleanUpSize);
    }
    
    public void printCurrentState() {
	log.info("Current State of Buckets");
	lastAccessTime.forEach((username, lastAccess) -> {
	    log.info("User : {} lastAccess : {}",username,lastAccess);
	});
	
	for(Map.Entry<String, Instant> entry : lastAccessTime.entrySet()) {
	    String username = entry.getKey();
	    Instant lastAccess = entry.getValue();
	}
	
    }

}
