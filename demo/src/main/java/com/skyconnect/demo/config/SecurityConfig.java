package com.skyconnect.demo.config;

import com.skyconnect.demo.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // ==========================================
                // CSRF
                // ==========================================

                .csrf(csrf -> csrf.disable())


                // ==========================================
                // SESSION
                // JWT = STATELESS
                // ==========================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // ==========================================
                // AUTHORIZATION
                // ==========================================

                .authorizeHttpRequests(auth -> auth

                        // Authentication APIs
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()


                        // Flights can be viewed without login
                        .requestMatchers(
                                "/api/flights/**"
                        ).permitAll()


                        // Booking requires JWT
                        .requestMatchers(
                                "/api/bookings/**"
                        ).authenticated()


                        // Passenger APIs require JWT
                        .requestMatchers(
                                "/api/passengers/**"
                        ).authenticated()


                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )


                // ==========================================
                // NO JWT / INVALID JWT
                // ==========================================

                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_UNAUTHORIZED
                                    );

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.getWriter().write(
                                            """
                                            {
                                                "success": false,
                                                "message": "Authentication required. Please provide a valid JWT token.",
                                                "data": null
                                            }
                                            """
                                    );
                                }
                        )


                        // ==================================
                        // AUTHENTICATED BUT NO PERMISSION
                        // ==================================

                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_FORBIDDEN
                                    );

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.getWriter().write(
                                            """
                                            {
                                                "success": false,
                                                "message": "Access denied. You do not have permission.",
                                                "data": null
                                            }
                                            """
                                    );
                                }
                        )
                )


                // ==========================================
                // JWT FILTER
                // ==========================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}