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
 * Configures application security using Spring Security.
 * <p>
 * Sets up HTTP Basic authentication, endpoint access rules,
 * and an in-memory user store with Employee and Manager roles.
 * </p>
 */
@Configuration
public class SecurityConfig {

    /**
     * Configures HTTP security, including endpoint access rules and CSRF settings.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
        http.csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/h2-console/**"
                        ).permitAll()
                        .requestMatchers("/api/employees/**").hasAnyRole("EMPLOYEE","MANAGER")
                        .requestMatchers("/api/leaves/apply","/api/leaves/history/**","/api/leaves/pending").hasRole("EMPLOYEE")
                        .requestMatchers("/api/manager/**").hasRole("MANAGER")
                        .anyRequest().denyAll())
                        .httpBasic(Customizer.withDefaults());
        // Disable X-Frame-Options to allow H2 console to be displayed in browser
        http.headers(headers->headers.frameOptions(frame->frame.disable()));
        return http.build();
    }

    /**
     * Provides an in-memory user store with one Employee and one Manager user.
     */
    @Bean
    public UserDetailsService userDetailsService(){
        UserDetails manager= User.builder()
                .username("manager")
                .password("{noop}123")
                .roles("MANAGER")
                .build();

        UserDetails employee =User.builder()
                .username("employee")
                .password("{noop}123")
                .roles("EMPLOYEE")
                .build();
        return new InMemoryUserDetailsManager(manager,employee);
    }
}
