package com.secure.notes.models;

// Importing necessary libraries for JSON serialization, persistence, and annotations

import com.fasterxml.jackson.annotation.JsonBackReference; // Avoids circular references in JSON serialization
import jakarta.persistence.*; // JPA annotations for mapping the entity to the database
import lombok.AllArgsConstructor; // Generates a constructor with all fields as arguments
import lombok.Data; // Generates boilerplate code like getters, setters, toString, etc.
import lombok.NoArgsConstructor; // Generates a no-argument constructor
import lombok.ToString; // Controls the string representation of the object

import java.util.HashSet; // Provides a default implementation for the Set
import java.util.Set; // Represents a collection of unique elements

// Marks this class as a JPA entity mapped to the "roles" database table
@Entity
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields as arguments
@Data // Lombok annotation to generate getters, setters, and other boilerplate code
@Table(name = "roles") // Maps this entity to the "roles" table
public class Role {

    // Primary key for the entity, auto-generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Uses the database's auto-increment feature
    @Column(name = "role_id") // Maps this field to the "role_id" column in the database
    private Integer roleId;

    @ToString.Exclude // Excludes this field from the `toString` method
    @Enumerated(EnumType.STRING) // Stores the enum value as a string in the database
    @Column(length = 20, name = "role_name") // Specifies the column name and maximum length for the role name
    private AppRole roleName;

    // Defines a one-to-many relationship with the User entity
    @OneToMany(
            mappedBy = "role", // Specifies the field in the User entity that maps to this relationship
            fetch = FetchType.LAZY, // Lazy loading for performance optimization
            cascade = {CascadeType.MERGE} // Specifies cascade behavior for related entities
    )
    @JsonBackReference // Prevents circular references during JSON serialization
    @ToString.Exclude // Excludes this field from the `toString` method
    private Set<User> users = new HashSet<>(); // Initializes the set to store associated users

    // Constructor for creating a Role with just the roleName
    public Role(AppRole roleName) {
        this.roleName = roleName;
    }
}
