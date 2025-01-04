// Package for security configuration
package com.secure.notes.security;

// Importing necessary dependencies

import com.secure.notes.models.AppRole;
import com.secure.notes.models.Role;
import com.secure.notes.models.User;
import com.secure.notes.repositories.RoleRepository;
import com.secure.notes.repositories.UserRepository;
import com.secure.notes.security.jwt.AuthEntryPointJwt;
import com.secure.notes.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.time.LocalDate;

import static org.springframework.security.config.Customizer.withDefaults;

// Marks this class as a Spring configuration class
@Configuration
// Enables web security for the application
@EnableWebSecurity
// Enables method-level security annotations
//@EnableMethodSecurity(prePostEnabled = true, // Allows usage of @PreAuthorize annotations
//        securedEnabled = true,               // Allows usage of @Secured annotations
//        jsr250Enabled = true)                // Allows usage of @RolesAllowed annotations
public class SecurityConfig {

    // Autowired entry point for handling unauthorized access
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // Bean to create an instance of the JWT token filter
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // Bean to configure the security filter chain
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // Configures Cross-Site Request Forgery (CSRF) protection
//        http.csrf(csrf ->
//                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // CSRF token stored in cookies
//                        .ignoringRequestMatchers("/api/auth/public/**")); // Public endpoints are excluded from CSRF protection
        http.csrf(AbstractHttpConfigurer::disable);
        // Configures role-based access and authorization rules
        http.authorizeHttpRequests((requests) ->
                requests
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Admin endpoints restricted to ADMIN role
                        .requestMatchers("/api/csrf-token").permitAll()    // CSRF token endpoint is publicly accessible
                        .requestMatchers("/api/auth/public/**").permitAll() // Public endpoints are accessible by all
                        .anyRequest().authenticated());                     // All other endpoints require authentication

        // Configures exception handling for unauthorized access
        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(unauthorizedHandler)); // Handles unauthorized access attempts

        // Adds a custom JWT authentication filter before the default UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);

        // Enables default form-based login and HTTP Basic authentication
        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());

        // Builds and returns the SecurityFilterChain
        return http.build();
    }

    // Bean to provide the AuthenticationManager for handling authentication
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Bean to provide a password encoder using BCrypt hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean to initialize default roles and users at application startup
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            // Ensures that the USER role exists in the database
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));

            // Ensures that the ADMIN role exists in the database
            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

            // Creates a default user with ROLE_USER if it does not already exist
            if (!userRepository.existsByUserName("user1")) {
                User user1 = new User("user1", "user1@example.com",
                        passwordEncoder.encode("password1")); // Password is securely hashed
                user1.setAccountNonLocked(false);             // User account is locked
                user1.setAccountNonExpired(true);             // Account is not expired
                user1.setCredentialsNonExpired(true);         // Credentials are valid
                user1.setEnabled(true);                       // User account is enabled
                user1.setCredentialsExpiryDate(LocalDate.now().plusYears(1)); // Set credentials expiry date
                user1.setAccountExpiryDate(LocalDate.now().plusYears(1));     // Set account expiry date
                user1.setTwoFactorEnabled(false);             // Two-factor authentication is disabled
                user1.setSignUpMethod("email");               // Indicates the sign-up method
                user1.setRole(userRole);                      // Assigns the USER role
                userRepository.save(user1);                   // Saves the user to the database
            }

            // Creates a default admin user with ROLE_ADMIN if it does not already exist
            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", "admin@example.com",
                        passwordEncoder.encode("adminPass")); // Password is securely hashed
                admin.setAccountNonLocked(true);              // Admin account is not locked
                admin.setAccountNonExpired(true);             // Account is not expired
                admin.setCredentialsNonExpired(true);         // Credentials are valid
                admin.setEnabled(true);                       // Admin account is enabled
                admin.setCredentialsExpiryDate(LocalDate.now().plusYears(1)); // Set credentials expiry date
                admin.setAccountExpiryDate(LocalDate.now().plusYears(1));     // Set account expiry date
                admin.setTwoFactorEnabled(false);             // Two-factor authentication is disabled
                admin.setSignUpMethod("email");               // Indicates the sign-up method
                admin.setRole(adminRole);                     // Assigns the ADMIN role
                userRepository.save(admin);                   // Saves the admin to the database
            }
        };
    }
}
