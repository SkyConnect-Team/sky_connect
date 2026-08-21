package com.skyconnect.demo.config;

import com.skyconnect.demo.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    // =====================================================
    // PASSWORD ENCODER
    // =====================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // =====================================================
    // AUTHENTICATION MANAGER
    // =====================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }


    // =====================================================
    // SECURITY FILTER CHAIN
    // =====================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // =================================================
                // CSRF
                // =================================================

                .csrf(csrf -> csrf.disable())


                // =================================================
                // JWT = STATELESS
                // =================================================

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                // =================================================
                // AUTHORIZATION
                // =================================================

                .authorizeHttpRequests(auth -> auth

                        // =========================================
                        // REGISTER + LOGIN
                        // No JWT required
                        // =========================================

                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // =========================================
                        // FLIGHT GET
                        // Anyone can view flights
                        // =========================================

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/flights/**"
                        ).permitAll()


                        // =========================================
                        // FLIGHT POST
                        // ONLY ADMIN
                        // =========================================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/flights/**"
                        ).hasRole("ADMIN")

                        // =========================================
                        // FLIGHT PUT
                        // ONLY ADMIN
                        // =========================================

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/flights/**"
                        ).hasRole("ADMIN")


                        // =========================================
                        // FLIGHT DELETE
                        // ONLY ADMIN
                        // =========================================

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/flights/**"
                        ).hasRole("ADMIN")


                        // =========================================
                        // BOOKINGS
                        // CUSTOMER + ADMIN
                        // JWT REQUIRED
                        // =========================================

                        .requestMatchers(
                                "/api/bookings/**"
                        ).authenticated()


                        // =========================================
                        // PASSENGERS
                        // CUSTOMER + ADMIN
                        // JWT REQUIRED
                        // =========================================

                        .requestMatchers(
                                "/api/passengers/**"
                        ).authenticated()


                        // =========================================
                        // EVERYTHING ELSE
                        // JWT REQUIRED
                        // =========================================

                        .anyRequest().authenticated()
                )


                // =================================================
                // EXCEPTION HANDLING
                // =================================================

                .exceptionHandling(exception -> exception

                        // =========================================
                        // 401 UNAUTHORIZED
                        // No JWT / Invalid JWT
                        // =========================================

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


                        // =========================================
                        // 403 FORBIDDEN
                        // JWT valid but insufficient permission
                        // =========================================

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


                // =================================================
                // JWT FILTER
                // =================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}