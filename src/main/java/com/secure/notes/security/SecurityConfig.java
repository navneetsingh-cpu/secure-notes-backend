// Specifies that this class is a configuration class for Spring
@Configuration
// Enables Spring Security in the application
@EnableWebSecurity
// Enables method-level security annotations like @PreAuthorize, @Secured, and @RolesAllowed
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    // Injects a custom authentication entry point for handling unauthorized access
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // Bean to provide a custom JWT authentication filter
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // Configures the security filter chain
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // Configures CSRF protection using cookies, but excludes specific endpoints
        http.csrf(csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/auth/public/**")
        );

        // Configures URL-based authorization
        http.authorizeHttpRequests((requests) ->
                requests
                        // Allows only users with the ADMIN role to access "/api/admin/**"
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Allows public access to "/api/csrf-token"
                        .requestMatchers("/api/csrf-token").permitAll()
                        // Allows public access to "/api/auth/public/**"
                        .requestMatchers("/api/auth/public/**").permitAll()
                        // Requires authentication for all other endpoints
                        .anyRequest().authenticated()
        );

        // Sets a custom authentication entry point for handling unauthorized access
        http.exceptionHandling(exception ->
                exception.authenticationEntryPoint(unauthorizedHandler)
        );

        // Adds the custom JWT authentication filter before the username/password authentication filter
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // Enables default form-based login
        http.formLogin(withDefaults());

        // Enables HTTP Basic authentication
        http.httpBasic(withDefaults());

        // Builds and returns the security filter chain
        return http.build();
    }

    // Bean to expose the authentication manager used by Spring Security
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Bean to provide a BCrypt password encoder for hashing passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Initializes some default roles and users in the application
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Ensures the ROLE_USER exists in the database, creates it if not found
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));

            // Ensures the ROLE_ADMIN exists in the database, creates it if not found
            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

            // Checks if a user "user1" exists, and if not, creates and saves it
            if (!userRepository.existsByUserName("user1")) {
                User user1 = new User("user1", "user1@example.com",
                        passwordEncoder.encode("password1")); // Password is hashed
                user1.setAccountNonLocked(false); // Account is initially locked
                user1.setAccountNonExpired(true); // Account is not expired
                user1.setCredentialsNonExpired(true); // Credentials are not expired
                user1.setEnabled(true); // Account is enabled
                user1.setCredentialsExpiryDate(LocalDate.now().plusYears(1)); // Sets credentials expiry date
                user1.setAccountExpiryDate(LocalDate.now().plusYears(1)); // Sets account expiry date
                user1.setTwoFactorEnabled(false); // Two-factor authentication is disabled
                user1.setSignUpMethod("email"); // Sets signup method as email
                user1.setRole(userRole); // Assigns the user role
                userRepository.save(user1); // Saves the user in the database
            }

            // Checks if a user "admin" exists, and if not, creates and saves it
            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", "admin@example.com",
                        passwordEncoder.encode("adminPass")); // Password is hashed
                admin.setAccountNonLocked(true); // Account is unlocked
                admin.setAccountNonExpired(true); // Account is not expired
                admin.setCredentialsNonExpired(true); // Credentials are not expired
                admin.setEnabled(true); // Account is enabled
                admin.setCredentialsExpiryDate(LocalDate.now().plusYears(1)); // Sets credentials expiry date
                admin.setAccountExpiryDate(LocalDate.now().plusYears(1)); // Sets account expiry date
                admin.setTwoFactorEnabled(false); // Two-factor authentication is disabled
                admin.setSignUpMethod("email"); // Sets signup method as email
                admin.setRole(adminRole); // Assigns the admin role
                userRepository.save(admin); // Saves the admin in the database
            }
        };
    }
}
