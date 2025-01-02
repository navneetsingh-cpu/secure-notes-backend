package com.secure.notes.models;

// Importing required libraries for JSON serialization, validation, and persistence

import com.fasterxml.jackson.annotation.JsonBackReference; // Avoids circular references in JSON serialization
import com.fasterxml.jackson.annotation.JsonIgnore; // Excludes fields from JSON serialization
import jakarta.persistence.*; // JPA annotations for mapping the entity to the database
import jakarta.validation.constraints.Email; // Validates email format
import jakarta.validation.constraints.NotBlank; // Ensures the field is not null or blank
import jakarta.validation.constraints.Size; // Restricts the size of the field
import lombok.Data; // Generates boilerplate code like getters, setters, toString, etc.
import lombok.NoArgsConstructor; // Generates a no-argument constructor
import lombok.ToString; // Controls string representation of the object
import org.hibernate.annotations.CreationTimestamp; // Automatically sets the creation timestamp
import org.hibernate.annotations.UpdateTimestamp; // Automatically updates the timestamp on changes

import java.time.LocalDate; // Represents dates
import java.time.LocalDateTime; // Represents date and time

// Marks the class as a JPA entity mapped to a database table
@Entity
@Data // Lombok annotation to generate getters, setters, and more
@NoArgsConstructor // Lombok annotation to create a no-argument constructor
@Table( // Specifies the database table and constraints
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"), // Ensures username is unique
                @UniqueConstraint(columnNames = "email") // Ensures email is unique
        }
)
public class User {

    // Primary key for the entity, auto-generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Uses database's auto-increment feature
    @Column(name = "user_id") // Maps this field to the `user_id` column in the database
    private Long userId;

    @NotBlank // Ensures the field is not null or blank
    @Size(max = 20) // Restricts the maximum length to 20 characters
    @Column(name = "username") // Maps this field to the `username` column
    private String userName;

    @NotBlank // Field cannot be null or blank
    @Size(max = 50) // Restricts the maximum length to 50 characters
    @Email // Validates the field as a valid email
    @Column(name = "email") // Maps to the `email` column
    private String email;

    @Size(max = 120) // Restricts the maximum length to 120 characters
    @Column(name = "password") // Maps to the `password` column
    @JsonIgnore // Excludes the field from JSON responses
    private String password;

    // Flags for account security and status
    private boolean accountNonLocked = true; // Indicates if the account is locked
    private boolean accountNonExpired = true; // Indicates if the account is expired
    private boolean credentialsNonExpired = true; // Indicates if credentials are expired
    private boolean enabled = true; // Indicates if the account is enabled

    // Additional account and credential expiry details
    private LocalDate credentialsExpiryDate; // Expiry date for credentials
    private LocalDate accountExpiryDate; // Expiry date for the account

    // Two-factor authentication details
    private String twoFactorSecret; // Secret key for two-factor authentication
    private boolean isTwoFactorEnabled = false; // Flag for enabling two-factor authentication
    private String signUpMethod; // Method of signup (e.g., email, Google, etc.)

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE}) // Defines a many-to-one relationship
    @JoinColumn(name = "role_id", referencedColumnName = "role_id") // Joins the `role_id` foreign key
    @JsonBackReference // Prevents circular references during JSON serialization
    @ToString.Exclude // Excludes the role from the `toString` method
    private Role role;

    @CreationTimestamp // Automatically populates with the entity's creation timestamp
    @Column(updatable = false) // Prevents updating this field after creation
    private LocalDateTime createdDate;

    @UpdateTimestamp // Automatically updates with the last modified timestamp
    private LocalDateTime updatedDate;

    // Constructor for creating a user with username, email, and password
    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    // Constructor for creating a user with username and email
    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    // Overrides the equals method to compare users by `userId`
    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Checks if both objects are the same
        if (!(o instanceof User)) return false; // Checks if the object is of type User
        return userId != null && userId.equals(((User) o).getUserId()); // Compares userId values
    }

    // Overrides the hashCode method for consistent hashing
    @Override
    public int hashCode() {
        return getClass().hashCode(); // Returns the hash code of the class
    }
}
