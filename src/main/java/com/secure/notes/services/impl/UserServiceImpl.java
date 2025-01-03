// Package declaration: Organizes this class into the `com.secure.notes.services.impl` namespace.
package com.secure.notes.services.impl;

// Importing necessary classes and packages used in this implementation.

import com.secure.notes.dtos.UserDTO; // Data Transfer Object for User.
import com.secure.notes.models.AppRole; // Enum or class representing application roles.
import com.secure.notes.models.Role; // Model for roles in the application.
import com.secure.notes.models.User; // Model for user entities.
import com.secure.notes.repositories.RoleRepository; // Repository interface for Role database operations.
import com.secure.notes.repositories.UserRepository; // Repository interface for User database operations.
import com.secure.notes.services.UserService; // UserService interface.
import org.springframework.beans.factory.annotation.Autowired; // For dependency injection.
import org.springframework.stereotype.Service; // Marks this class as a Spring Service component.

import java.util.List; // For handling collections like lists.

// Marks this class as a Service in the Spring application context.
@Service
public class UserServiceImpl implements UserService {

    // Injecting dependencies for database operations.
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    /**
     * Updates a user's role by their ID.
     *
     * @param userId   The ID of the user whose role is to be updated.
     * @param roleName The name of the role to assign to the user.
     */
    @Override
    public void updateUserRole(Long userId, String roleName) {
        // Retrieve the user by ID or throw an exception if not found.
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));

        // Convert the role name to an AppRole enum instance.
        AppRole appRole = AppRole.valueOf(roleName);

        // Retrieve the Role entity based on the AppRole or throw an exception if not found.
        Role role = roleRepository.findByRoleName(appRole)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Set the retrieved role to the user and save the updated user entity.
        user.setRole(role);
        userRepository.save(user);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of all User entities.
     */
    @Override
    public List<User> getAllUsers() {
        // Fetches all users from the UserRepository.
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by their ID and converts it to a UserDTO.
     *
     * @param id The ID of the user to retrieve.
     * @return A UserDTO object representing the user's details.
     */
    @Override
    public UserDTO getUserById(Long id) {
        // Retrieve the user by ID or throw an exception if not found.
        User user = userRepository.findById(id).orElseThrow();

        // Convert the retrieved user entity to a UserDTO and return it.
        return convertToDto(user);
    }

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user The User entity to convert.
     * @return A UserDTO object containing the user's details.
     */
    private UserDTO convertToDto(User user) {
        // Constructs a UserDTO by mapping fields from the User entity.
        return new UserDTO(
                user.getUserId(), // The user's unique ID.
                user.getUserName(), // The user's name.
                user.getEmail(), // The user's email address.
                user.isAccountNonLocked(), // Whether the account is locked.
                user.isAccountNonExpired(), // Whether the account is expired.
                user.isCredentialsNonExpired(), // Whether the credentials are expired.
                user.isEnabled(), // Whether the account is enabled.
                user.getCredentialsExpiryDate(), // Date when credentials expire.
                user.getAccountExpiryDate(), // Date when account expires.
                user.getTwoFactorSecret(), // Two-factor authentication secret.
                user.isTwoFactorEnabled(), // Whether two-factor authentication is enabled.
                user.getSignUpMethod(), // Method the user signed up with.
                user.getRole(), // The user's assigned role.
                user.getCreatedDate(), // Date the user was created.
                user.getUpdatedDate() // Date the user was last updated.
        );
    }
}
