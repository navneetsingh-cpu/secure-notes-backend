package com.secure.notes.security.services;

// Necessary imports for user management and Spring Security

import com.secure.notes.models.User; // The User entity class
import com.secure.notes.repositories.UserRepository; // Repository for accessing User data
import org.springframework.beans.factory.annotation.Autowired; // For dependency injection
import org.springframework.security.core.userdetails.UserDetails; // Represents user details for Spring Security
import org.springframework.security.core.userdetails.UserDetailsService; // Spring Security interface for loading user data
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Exception thrown when a user is not found
import org.springframework.stereotype.Service; // Marks the class as a Spring-managed service
import org.springframework.transaction.annotation.Transactional; // Ensures the method's database operations are transactional

// Marks this class as a Spring Service to manage user details
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Injects the UserRepository for interacting with the database
    @Autowired
    UserRepository userRepository;

    /**
     * Loads the user details by username.
     *
     * @param username the username of the user to load
     * @return a UserDetails object containing user details for authentication
     * @throws UsernameNotFoundException if the user is not found in the database
     */
    @Override
    @Transactional // Ensures that database operations are executed within a transaction
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetches the user from the repository by username
        User user = userRepository.findByUserName(username)
                // Throws an exception if the user is not found
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // Converts the User entity into a UserDetails object for Spring Security
        return UserDetailsImpl.build(user);
    }
}
