package com.flipkart.ecommerce_backend.security.config;

import com.flipkart.ecommerce_backend.security.filter.JwtAuthenticationFilter;
import com.flipkart.ecommerce_backend.services.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
  @Autowired private UserManagementService userDetailsService;
  @Autowired private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // configure chained method style
    http.csrf().disable()
        .cors().disable()
        .httpBasic().disable()
        .formLogin().disable();

    // Configure stateless session management
    http
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    // Add the JWT filter before the standard UsernamePasswordAuthenticationFilter
    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    // Configure authorization rules
    http.authorizeHttpRequests()
        .requestMatchers(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/referesh-token",
            "/api/verify/email",
            "/api/password/forgot",
            "/api/password/reset",
            "/api/products/**",
            "/api-docs./**",
            "/swagger-ui/**",
            "/v3/api-docs/**")
        .permitAll()
        .anyRequest()
        .authenticated(); // Allow public access to these specific paths

    return http.build();
  }
}
