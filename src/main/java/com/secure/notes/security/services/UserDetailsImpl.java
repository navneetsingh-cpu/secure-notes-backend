package com.secure.notes.security.services;

// Necessary imports for collections, security, and JSON annotations
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.secure.notes.models.User; // Importing the User model
import lombok.Data; // Lombok annotation to generate getters, setters, etc.
import lombok.NoArgsConstructor; // Lombok annotation to generate a no-argument constructor
import org.springframework.security.core.GrantedAuthority; // Represents an authority granted to a user
import org.springframework.security.core.authority.SimpleGrantedAuthority; // A simple implementation of GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails; // Spring Security interface for user authentication

import com.fasterxml.jackson.annotation.JsonIgnore; // Prevents serialization of sensitive fields

// Lombok annotations for boilerplate code
@NoArgsConstructor // Generates a no-argument constructor
@Data // Generates getters, setters, equals, hashCode, and toString methods
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L; // Ensures class compatibility during serialization

    private Long id; // Stores the user's unique identifier
    private String username; // Stores the user's username
    private String email; // Stores the user's email

    @JsonIgnore // Prevents this field from being included in JSON responses
    private String password; // Stores the user's password

    private boolean is2faEnabled; // Indicates if two-factor authentication is enabled

    private Collection<? extends GrantedAuthority> authorities; // Collection of authorities granted to the user

    // Constructor to initialize the UserDetailsImpl instance
    public UserDetailsImpl(Long id, String username, String email, String password,
                           boolean is2faEnabled, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.is2faEnabled = is2faEnabled;
        this.authorities = authorities;
    }

    // Static factory method to create an instance of UserDetailsImpl from a User entity
    public static UserDetailsImpl build(User user) {
        // Maps the user's role to a SimpleGrantedAuthority
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole().getRoleName().name());

        // Creates a new UserDetailsImpl instance
        return new UserDetailsImpl(
                user.getUserId(), // User ID
                user.getUserName(), // Username
                user.getEmail(), // Email
                user.getPassword(), // Password
                user.isTwoFactorEnabled(), // Two-factor authentication flag
                List.of(authority) // Wraps the authority in a list
        );
    }

    // Returns the authorities granted to the user
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // Returns the user's unique identifier
    public Long getId() {
        return id;
    }

    // Returns the user's email
    public String getEmail() {
        return email;
    }

    // Returns the user's password
    @Override
    public String getPassword() {
        return password;
    }

    // Returns the user's username
    @Override
    public String getUsername() {
        return username;
    }

    // Indicates if the user's account is non-expired (always true in this implementation)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Indicates if the user's account is non-locked (always true in this implementation)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Indicates if the user's credentials are non-expired (always true in this implementation)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Indicates if the user's account is enabled (always true in this implementation)
    @Override
    public boolean isEnabled() {
        return true;
    }

    // Custom getter for the two-factor authentication flag
    public boolean is2faEnabled() {
        return is2faEnabled;
    }

    // Overrides the equals method to compare users based on their ID
    @Override
    public boolean equals(Object o) {
        if (this == o) // Checks for reference equality
            return true;
        if (o == null || getClass() != o.getClass()) // Ensures the other object is of the same class
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id); // Compares the IDs of the two users
    }
}
