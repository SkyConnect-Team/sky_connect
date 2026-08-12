package com.skyconnect.demo.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // Disable CSRF for REST API during development
                .csrf(csrf -> csrf.disable())

                // Allow these APIs without authentication
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/flights/**",
                                "/api/bookings/**",
                                "/api/passengers/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}