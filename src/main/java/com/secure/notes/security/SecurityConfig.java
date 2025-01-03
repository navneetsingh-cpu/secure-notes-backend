// Package declaration: Groups the class under the `com.secure.notes.security` package.
package com.secure.notes.security;

// Importing necessary classes and packages for security and database initialization.

import com.secure.notes.models.AppRole; // Enum for application roles.
import com.secure.notes.models.Role; // Model representing a Role.
import com.secure.notes.models.User; // Model representing a User.
import com.secure.notes.repositories.RoleRepository; // Repository interface for Role database operations.
import com.secure.notes.repositories.UserRepository; // Repository interface for User database operations.
import org.springframework.boot.CommandLineRunner; // For initializing data at application startup.
import org.springframework.context.annotation.Bean; // Indicates a method produces a Spring Bean.
import org.springframework.context.annotation.Configuration; // Marks this class as a configuration class.
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Enables method-level security.
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Configures web-based security.
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Enables web security.
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Allows customization of web security settings.
import org.springframework.security.web.SecurityFilterChain; // Defines the security filter chain.

import java.time.LocalDate; // Handles date operations.

import static org.springframework.security.config.Customizer.withDefaults; // Provides default security configurations.

// Marks this class as a configuration class for Spring Security.
@Configuration
@EnableWebSecurity // Enables Spring Security for the application.
//@EnableMethodSecurity(
//        prePostEnabled = true, // Enables `@PreAuthorize` and `@PostAuthorize`.
//        securedEnabled = true, // Enables `@Secured` annotations.
//        jsr250Enabled = true // Enables `@RolesAllowed` annotations.
//)
public class SecurityConfig {

    /**
     * Configures the default security filter chain for the application.
     *
     * @param http The HttpSecurity object to configure.
     * @return A configured SecurityFilterChain bean.
     * @throws Exception if an error occurs during configuration.
     */
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // Configure all requests to require authentication.
        http.authorizeHttpRequests((requests)
                -> requests
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated());

        // Disable CSRF protection (not recommended for production without additional context).
        http.csrf(AbstractHttpConfigurer::disable);

        // Enable HTTP Basic authentication (credentials passed via headers).
        http.httpBasic(withDefaults());

        // Build and return the configured security filter chain.
        return http.build();
    }

    /**
     * Initializes default roles and users in the database during application startup.
     *
     * @param roleRepository The repository for Role operations.
     * @param userRepository The repository for User operations.
     * @return A CommandLineRunner that initializes data.
     */
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {
            // Ensure the existence of the "ROLE_USER" role, or create it if not present.
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));

            // Ensure the existence of the "ROLE_ADMIN" role, or create it if not present.
            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

            // Create a default user account if it doesn't already exist.
            if (!userRepository.existsByUserName("user1")) {
                User user1 = new User("user1", "user1@example.com", "{noop}password1"); // `{noop}` disables password encoding.
                user1.setAccountNonLocked(false); // The account is locked.
                user1.setAccountNonExpired(true); // The account is not expired.
                user1.setCredentialsNonExpired(true); // Credentials are not expired.
                user1.setEnabled(true); // The account is enabled.
                user1.setCredentialsExpiryDate(LocalDate.now().plusYears(1)); // Sets credentials expiry date to one year from now.
                user1.setAccountExpiryDate(LocalDate.now().plusYears(1)); // Sets account expiry date to one year from now.
                user1.setTwoFactorEnabled(false); // Disables two-factor authentication.
                user1.setSignUpMethod("email"); // Specifies the sign-up method.
                user1.setRole(userRole); // Assigns the "ROLE_USER" role.
                userRepository.save(user1); // Saves the user to the database.
            }

            // Create a default admin account if it doesn't already exist.
            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", "admin@example.com", "{noop}adminPass");
                admin.setAccountNonLocked(true); // The account is not locked.
                admin.setAccountNonExpired(true); // The account is not expired.
                admin.setCredentialsNonExpired(true); // Credentials are not expired.
                admin.setEnabled(true); // The account is enabled.
                admin.setCredentialsExpiryDate(LocalDate.now().plusYears(1)); // Sets credentials expiry date to one year from now.
                admin.setAccountExpiryDate(LocalDate.now().plusYears(1)); // Sets account expiry date to one year from now.
                admin.setTwoFactorEnabled(false); // Disables two-factor authentication.
                admin.setSignUpMethod("email"); // Specifies the sign-up method.
                admin.setRole(adminRole); // Assigns the "ROLE_ADMIN" role.
                userRepository.save(admin); // Saves the admin to the database.
            }
        };
    }
}
