package com.skyconnect.demo.service;

import com.skyconnect.demo.dto.request.LoginRequest;
import com.skyconnect.demo.dto.request.RegisterRequest;
import com.skyconnect.demo.dto.response.AuthResponse;
import com.skyconnect.demo.entity.User;
import com.skyconnect.demo.enums.Role;
import com.skyconnect.demo.repository.UserRepository;
import com.skyconnect.demo.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    // =====================================================
    // REGISTER
    // CUSTOMER + ADMIN
    // =====================================================

    public AuthResponse register(
            RegisterRequest request
    ) {

        // ==========================================
        // CHECK EMAIL
        // ==========================================

        if (userRepository.existsByEmail(
                request.getEmail()
        )) {

            throw new RuntimeException(
                    "Email already registered"
            );
        }


        // ==========================================
        // DETERMINE ROLE
        // ==========================================

        Role role;

        if (request.getAdminCode() != null
                && !request.getAdminCode().isBlank()) {

            // Admin code was provided

            if (!request.getAdminCode().equals(
                    "SKYADMIN2026"
            )) {

                throw new RuntimeException(
                        "Invalid admin code"
                );
            }

            role = Role.ADMIN;

        } else {

            // No admin code → normal customer

            role = Role.CUSTOMER;
        }


        // ==========================================
        // CREATE USER
        // ==========================================

        User user = User.builder()

                .name(
                        request.getName()
                )

                .email(
                        request.getEmail()
                )

                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )

                .phone(
                        request.getPhone()
                )

                .role(
                        role
                )

                .build();


        // ==========================================
        // SAVE USER
        // ==========================================

        User savedUser =
                userRepository.save(user);


        // ==========================================
        // GENERATE JWT
        // ==========================================

        String token =
                jwtService.generateToken(

                        savedUser.getEmail(),

                        savedUser.getRole().name()
                );


        // ==========================================
        // RESPONSE
        // ==========================================

        return new AuthResponse(

                token,

                "Bearer",

                savedUser.getId(),

                savedUser.getName(),

                savedUser.getEmail(),

                savedUser.getRole().name()
        );
    }


    // =====================================================
    // LOGIN
    // =====================================================

    public AuthResponse login(
            LoginRequest request
    ) {

        User user =
                userRepository.findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid email or password"
                                )
                        );


        if (!passwordEncoder.matches(

                request.getPassword(),

                user.getPassword()

        )) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }


        String token =
                jwtService.generateToken(

                        user.getEmail(),

                        user.getRole().name()
                );


        return new AuthResponse(

                token,

                "Bearer",

                user.getId(),

                user.getName(),

                user.getEmail(),

                user.getRole().name()
        );
    }
}