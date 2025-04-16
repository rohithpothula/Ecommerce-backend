package com.flipkart.ecommerce_backend.security.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.flipkart.ecommerce_backend.security.filter.RequestBodyCachingFilter;

@Configuration
public class FilterConfig {
    
    public FilterRegistrationBean<RequestBodyCachingFilter> requestBodyCachingFilterRegistration(){
	FilterRegistrationBean<RequestBodyCachingFilter> registration = new FilterRegistrationBean<>();
	registration.setFilter(new RequestBodyCachingFilter());
	registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
	registration.addUrlPatterns("/**");
	return registration;
    }

}
