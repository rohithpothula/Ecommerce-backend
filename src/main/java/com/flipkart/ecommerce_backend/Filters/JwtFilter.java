package com.flipkart.ecommerce_backend.Filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.flipkart.ecommerce_backend.models.LocalUser;
import com.flipkart.ecommerce_backend.models.Repository.LocalUserRepository;
import com.flipkart.ecommerce_backend.services.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private LocalUserRepository localUserRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String tokenHeader = request.getHeader("Authorization");
		if(tokenHeader!=null && tokenHeader.startsWith("Bearer")) {
			String jwt = tokenHeader.substring(7);
			String username = jwtService.getUserNameFromToken(jwt);
			Optional<LocalUser> optionalUser =  localUserRepository.findByUsernameIgnoreCase(username);
			if(optionalUser.isPresent()) {
				LocalUser localUser = optionalUser.get();
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(localUser, null, new ArrayList<>());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		filterChain.doFilter(request, response);
	}
}
