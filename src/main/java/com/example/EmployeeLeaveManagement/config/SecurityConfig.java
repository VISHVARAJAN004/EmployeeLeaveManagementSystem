package com.example.EmployeeLeaveManagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration class.
 * <p>This class configures HTTP security, basic authentication, and user details
 * for the Employee Leave Management System.</p>
 * <p>Key features:</p>
 * <ul>
 *     <li>Disables CSRF for simplicity (not recommended for production without proper consideration).</li>
 *     <li>Permits unrestricted access to Swagger UI, API documentation, and H2 console.</li>
 *     <li>Restricts all "/api/**" endpoints to users with the MANAGER role.</li>
 *     <li>Enables HTTP Basic authentication.</li>
 *     <li>Disables frame options to allow H2 console rendering in a browser frame.</li>
 * </ul>
 */
@Configuration
public class SecurityConfig {

    /**
     * Configures the HTTP security filter chain.
     * @param http HttpSecurity object provided by Spring Security
     * @return SecurityFilterChain configured for this application
     * @throws Exception if there is a configuration error
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http.csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/h2-console/**"
                        ).permitAll()
                        .requestMatchers("/api/**").hasRole("MANAGER")
                        .anyRequest().authenticated())
                        .httpBasic(Customizer.withDefaults());
        // Disable X-Frame-Options to allow H2 console to be displayed in browser
        http.headers(headers->headers.frameOptions(frame->frame.disable()));
        return http.build();
    }

    /**
     * Defines an in-memory user for testing purposes.
     * <p>The application uses an in-memory user with username "manager" and password "123".
     * The user has the role MANAGER.</p
     * @return UserDetailsService with the configured in-memory user
     */
    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails manager= User.builder()
                .username("manager")
                .password("{noop}123")
                .roles("MANAGER")
                .build();
        return new InMemoryUserDetailsManager(manager);
    }
}
