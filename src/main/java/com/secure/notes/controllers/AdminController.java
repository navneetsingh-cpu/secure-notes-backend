// Package declaration: Organizes this class into the `com.secure.notes.controllers` namespace.
package com.secure.notes.controllers;

// Importing necessary classes and packages used in this controller.

import com.secure.notes.dtos.UserDTO; // Importing UserDTO for data transfer.
import com.secure.notes.models.User; // Importing User model class.
import com.secure.notes.services.UserService; // Importing UserService to handle user-related business logic.
import org.springframework.beans.factory.annotation.Autowired; // For dependency injection.
import org.springframework.http.HttpStatus; // HTTP status codes.
import org.springframework.http.ResponseEntity; // To build HTTP responses.
import org.springframework.web.bind.annotation.*; // REST controller annotations.

import java.util.List; // To handle collections like lists.

// Marking this class as a REST controller to handle HTTP requests.
@RestController
// Base URL mapping for all endpoints in this controller.
@RequestMapping("/api/admin")
public class AdminController {

    // Injecting the UserService dependency to use its methods.
    @Autowired
    UserService userService;

    /**
     * Endpoint to retrieve all users.
     * <p>
     * HTTP GET request at `/api/admin/getusers`.
     *
     * @return A ResponseEntity containing a list of User objects and an HTTP status code.
     */
    @GetMapping("/getusers")
    public ResponseEntity<List<User>> getAllUsers() {
        // Calls the UserService to fetch all users and wraps the result in a ResponseEntity with HTTP status 200 (OK).
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    /**
     * Endpoint to update a user's role.
     * <p>
     * HTTP PUT request at `/api/admin/update-role`.
     *
     * @param userId   The ID of the user to update.
     * @param roleName The new role name to assign to the user.
     * @return A ResponseEntity containing a success message and HTTP status 200 (OK).
     */
    @PutMapping("/update-role")
    public ResponseEntity<String> updateUserRole(@RequestParam Long userId,
                                                 @RequestParam String roleName) {
        // Calls the UserService to update the user's role.
        userService.updateUserRole(userId, roleName);
        // Returns a success message with HTTP status 200 (OK).
        return ResponseEntity.ok("User role updated");
    }

    /**
     * Endpoint to retrieve a user by their ID.
     * <p>
     * HTTP GET request at `/api/admin/user/{id}`.
     *
     * @param id The ID of the user to retrieve.
     * @return A ResponseEntity containing the user's details (UserDTO) and HTTP status 200 (OK).
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        // Calls the UserService to fetch the user by their ID and wraps the result in a ResponseEntity with HTTP status 200 (OK).
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }
}
