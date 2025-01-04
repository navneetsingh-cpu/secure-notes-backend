package com.secure.notes.security.jwt;

// Importing required classes and packages for handling JWT and request filtering

import com.secure.notes.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Marking the class as a Spring-managed component
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    // Injecting the JwtUtils dependency to handle JWT-related operations
    @Autowired
    private JwtUtils jwtUtils;

    // Injecting the UserDetailsService implementation to load user details by username
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // Logger for logging messages and debugging information
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * Filters incoming requests and validates the JWT token.
     * If the token is valid, the user is authenticated and added to the SecurityContext.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("AuthTokenFilter called for URI: {}", request.getRequestURI()); // Log the incoming request URI

        try {
            // Extract the JWT token from the request
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) { // Validate the token
                // Extract the username from the JWT token
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // Load user details by username
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Create an authentication object with the user's details and roles
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null, // No credentials needed for JWT-based authentication
                                userDetails.getAuthorities()); // User's roles/authorities
                logger.debug("Roles from JWT: {}", userDetails.getAuthorities()); // Log the user's roles

                // Attach additional details about the request
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the authentication object in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e); // Log errors during authentication
        }

        // Continue the filter chain for further processing
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header in the HTTP request.
     */
    private String parseJwt(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromHeader(request); // Use JwtUtils to get the token
        logger.debug("AuthTokenFilter.java: {}", jwt); // Log the extracted token
        return jwt;
    }
}
