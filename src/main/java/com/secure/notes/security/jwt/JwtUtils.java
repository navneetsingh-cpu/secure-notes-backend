package com.secure.notes.security.jwt;

// Importing required classes and packages for JWT processing

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

// Declaring the class as a Spring component
@Component
public class JwtUtils {

    // Logger instance for logging messages and errors
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Injecting the JWT secret key from application properties
    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    // Injecting the JWT expiration time (in milliseconds) from application properties
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // Extracts the JWT token from the "Authorization" header of the HTTP request
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization"); // Retrieve the Authorization header
        logger.debug("Authorization Header: {}", bearerToken); // Log the header content
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove the "Bearer " prefix to get the token
        }
        return null; // Return null if no valid token is present
    }

    // Generates a JWT token using the username and expiration time
    public String generateTokenFromUsername(UserDetails userDetails) {
        String username = userDetails.getUsername(); // Get the username from UserDetails
        return Jwts.builder() // Build the JWT
                .subject(username) // Set the subject (username)
                .issuedAt(new Date()) // Set the issue date to current time
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Set expiration date
                .signWith(key()) // Sign the token with the secret key
                .compact(); // Generate the token as a compact string
    }

    // Extracts the username from the JWT token
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser() // Create a parser for the JWT
                .verifyWith((SecretKey) key()) // Verify the signature using the secret key
                .build().parseSignedClaims(token) // Parse the token to extract claims
                .getPayload().getSubject(); // Retrieve the subject (username) from claims
    }

    // Generates the cryptographic key from the secret key string
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret)); // Decode the base64-encoded secret and create the key
    }

    // Validates the JWT token for structure, signature, and expiration
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser() // Create a parser for the JWT
                    .verifyWith((SecretKey) key()) // Verify the signature using the secret key
                    .build().parseSignedClaims(authToken); // Parse and validate the token
            return true; // Token is valid
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage()); // Log if the token is malformed
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage()); // Log if the token is expired
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage()); // Log if the token is unsupported
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage()); // Log if the claims are empty
        }
        return false; // Token is invalid
    }
}
