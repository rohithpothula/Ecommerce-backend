package com.flipkart.ecommerce_backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.flipkart.ecommerce_backend.Filters.JwtFilter;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	@Autowired 
	private JwtFilter jwtFilter;
	
	@Bean
	  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable().cors().disable();
		http.addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class); 
		http.authorizeHttpRequests().requestMatchers("/api/products","/api/auth/registeruser","/api/auth/login","/api/auth/verify","/api/auth/forgotpassword").permitAll()
		.anyRequest().authenticated();
		return http.build();
	}
	  
}
 