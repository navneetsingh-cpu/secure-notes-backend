package com.secure.notes.controllers;

// Import necessary classes and annotations for REST API, authentication, and HTTP response handling

import com.secure.notes.security.jwt.JwtUtils;
import com.secure.notes.security.request.LoginRequest;
import com.secure.notes.security.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Mark this class as a REST controller and map its endpoints under "/api/auth"
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Inject the JwtUtils for generating and managing JWT tokens
    @Autowired
    JwtUtils jwtUtils;

    // Inject the AuthenticationManager for authenticating user credentials
    @Autowired
    AuthenticationManager authenticationManager;

    /**
     * Handles POST requests to "/public/signin" for user login.
     * Authenticates the user and returns a JWT token along with user roles if successful.
     */
    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication;

        try {
            // Attempt to authenticate the user with the provided username and password
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException exception) {
            // If authentication fails, return an error response with a 404 status
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials"); // Provide a user-friendly error message
            map.put("status", false); // Indicate failure in the response
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND); // Return 404 status
        }

        // If authentication is successful, set the authentication in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Retrieve the authenticated user's details
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generate a JWT token for the authenticated user
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        // Collect the user's roles from their authorities
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority()) // Extract the authority name (role)
                .collect(Collectors.toList());

        // Create a response object containing the username, roles, and JWT token
        LoginResponse response = new LoginResponse(
                userDetails.getUsername(),
                roles,
                jwtToken
        );

        // Return the response entity with the JWT token and user details in the response body
        return ResponseEntity.ok(response);
    }
}
