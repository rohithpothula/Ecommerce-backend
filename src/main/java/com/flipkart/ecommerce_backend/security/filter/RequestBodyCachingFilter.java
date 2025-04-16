package com.flipkart.ecommerce_backend.security.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.flipkart.ecommerce_backend.wrapper.CachedBodyHttpServletRequest;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class RequestBodyCachingFilter implements Filter{

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
	    throws IOException, ServletException {
	if(request instanceof HttpServletRequest) {
	    CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest((HttpServletRequest)request);
	    chain.doFilter(wrappedRequest, response);
	}
	else {
	    chain.doFilter(request, response);
	}
    }
}
