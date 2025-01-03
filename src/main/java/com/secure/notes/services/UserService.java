// Package declaration: Organizes classes into namespaces for better structure and avoids naming conflicts.
package com.secure.notes.services;

// Importing necessary classes and packages used in this interface.

import com.secure.notes.dtos.UserDTO;  // Importing the UserDTO class for data transfer objects.
import com.secure.notes.models.User;  // Importing the User model class representing users in the application.

import java.util.List;  // Importing the List interface for handling collections of data.

// Public interface: UserService defines the contract for services related to user operations.
public interface UserService {

    /**
     * Updates the role of a user identified by their userId.
     *
     * @param userId   The ID of the user whose role needs to be updated.
     * @param roleName The name of the new role to assign to the user.
     */
    void updateUserRole(Long userId, String roleName);

    /**
     * Retrieves a list of all users.
     *
     * @return A list containing User objects representing all users.
     */
    List<User> getAllUsers();

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id The ID of the user to retrieve.
     * @return A UserDTO object containing the user's details.
     */
    UserDTO getUserById(Long id);
}
