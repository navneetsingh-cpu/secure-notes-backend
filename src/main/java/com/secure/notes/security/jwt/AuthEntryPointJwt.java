package com.secure.notes.security.jwt;

// Importing required classes for handling unauthorized access and creating JSON responses

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Marking the class as a Spring-managed component
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    // Logger for logging unauthorized access attempts and debugging information
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * Handles authentication exceptions by sending an HTTP 401 Unauthorized response.
     * This method is triggered whenever an unauthenticated user attempts to access a protected resource.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // Log the error message for debugging purposes
        logger.error("Unauthorized error: {}", authException.getMessage());
        System.out.println(authException); // Print the exception to the console (for debugging)

        // Set the response content type to JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Set the HTTP status code to 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Create a response body containing error details
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED); // HTTP status code
        body.put("error", "Unauthorized"); // Error description
        body.put("message", authException.getMessage()); // Exception message (reason for unauthorized access)
        body.put("path", request.getServletPath()); // The requested URI that caused the exception

        // Convert the response body to JSON and write it to the response output stream
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
