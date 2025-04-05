package com.flipkart.ecommerce_backend.security.filter;

import com.flipkart.ecommerce_backend.security.exception.TokenException;
import com.flipkart.ecommerce_backend.services.TokenService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Dependencies injected via constructor, using interfaces
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService; // Depend on the interface

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, // Add @NonNull for clarity
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Extract token from header
            String jwt = extractTokenFromHeader(request);

            // 2. Validate token only if present and not already authenticated
            if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 3. Extract Claims (which implicitly validates signature/expiry via TokenService)
                Claims claims = tokenService.extractClaims(jwt);
                String username = claims.getSubject(); // Get username from claims

                // 4. Load user details if username is extracted
                if (username != null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                    // 5. Create authentication token if user is found
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null, // Credentials are null as we used JWT
                                    userDetails.getAuthorities());

                    // Optional: Set details like IP address, session ID
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 6. Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authentication successful for user: {}", username);
                }
            }
        } catch (TokenException e) {
            // Specific exception from our TokenService for validation failures
            log.warn("JWT Token processing error: {}", e.getMessage());
            SecurityContextHolder.clearContext(); // Ensure context is clear on error
            // Let Spring Security's ExceptionTranslationFilter handle sending the 401
            // response via the configured AuthenticationEntryPoint. Don't throw here
            // unless you have specific filter-level handling.
            // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage()); // Alternative: directly send error
        } catch (UsernameNotFoundException e) {
            log.warn("User not found for token subject: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            // Let ExceptionTranslationFilter handle this
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
            SecurityContextHolder.clearContext();
            // Let ExceptionTranslationFilter handle this
        }

        // 7. Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header.
     * Expects "Bearer <token>".
     *
     * @param request The incoming HttpServletRequest.
     * @return The token string or null if not found or invalid format.
     */
    private String extractTokenFromHeader(HttpServletRequest request) {
        String headerAuth = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER_PREFIX)) {
            return headerAuth.substring(BEARER_PREFIX.length());
        }
        log.trace("No JWT token found in Authorization header");
        return null;
    }
}
