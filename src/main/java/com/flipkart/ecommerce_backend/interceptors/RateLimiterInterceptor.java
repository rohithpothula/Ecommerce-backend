package com.flipkart.ecommerce_backend.interceptors;

import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.ecommerce_backend.dtos.LoginRequest;
import com.flipkart.ecommerce_backend.dtos.RegistrationRequest;
import com.flipkart.ecommerce_backend.dtos.ResetPasswordRequest;
import com.flipkart.ecommerce_backend.exception.auth.RateLimitExceptionLogin;
import com.flipkart.ecommerce_backend.handler.ErrorCode;
import com.flipkart.ecommerce_backend.security.config.RateLimitConfig;
import com.flipkart.ecommerce_backend.wrapper.CachedBodyHttpServletRequest;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RateLimiterInterceptor implements HandlerInterceptor {
    
    private static final String LOGIN_API_URI = "/api/auth/login";
    private static final String RESET_PASSWORD_API_URI = "/api/auth/reset";
    
    @Autowired
    private RateLimitConfig rateLimitConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	    throws Exception {
	
        if(request instanceof CachedBodyHttpServletRequest) {
            CachedBodyHttpServletRequest cacheRequest = new CachedBodyHttpServletRequest(request);
            
            String requestBody = cacheRequest.getbody();
            ObjectMapper objectMapper = new ObjectMapper();
            
            if(cacheRequest.getRequestURI()==LOGIN_API_URI) {
        	LoginRequest loginRequest = objectMapper.readValue(requestBody, LoginRequest.class);
                Bucket bucket = rateLimitConfig.resolveBucketForLogin(loginRequest.username());
                if(!bucket.tryConsume(1)) {
            	log.warn("Rate Limit exceed for login Attempts for user: ",loginRequest.username());
            	throw new RateLimitExceptionLogin(ErrorCode.RATE_LIMIT_ERROR_LOGIN,"Too Many Requests try again Later");
                }
            }
            else if(cacheRequest.getRequestURI()==RESET_PASSWORD_API_URI) {
        	ResetPasswordRequest resetPasswordRequest = objectMapper.readValue(requestBody, ResetPasswordRequest.class);
        	Bucket bucket = rateLimitConfig.resolveBucketForResetPassword(resetPasswordRequest.email());
        	if(!bucket.tryConsume(1)) {
        	    log.warn("Rate Limit exceed for Password Reset for email: ",resetPasswordRequest.email());
        	    throw new RateLimitExceptionLogin(ErrorCode.RATE_LIMIT_ERROR_RESET_PASSWORD,"Frequent Password reset, Please try again Later");
        	}
            }
        }
	return true;
    }
}
