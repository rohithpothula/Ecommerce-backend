package com.flipkart.ecommerce_backend.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.flipkart.ecommerce_backend.interceptors.RateLimiterInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimiterInterceptor rateLimiterInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(rateLimiterInterceptor)
		.addPathPatterns("/api/auth/login", "/api/auth/register")
		.order(Ordered.HIGHEST_PRECEDENCE);
    }

}
