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
    // =====================================================

    public AuthResponse register(
            RegisterRequest request
    ) {


        // Check existing email

        if (userRepository.existsByEmail(
                request.getEmail()
        )) {

            throw new RuntimeException(
                    "Email already registered"
            );
        }


        // Create user

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
                        Role.CUSTOMER
                )

                .build();


        User savedUser =
                userRepository.save(user);


        // Generate token immediately

        String token =
                jwtService.generateToken(
                        savedUser.getEmail(),
                        savedUser.getRole().name()
                );


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


        // Find user

        User user =
                userRepository.findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid email or password"
                                )
                        );


        // Check password

        if (!passwordEncoder.matches(

                request.getPassword(),

                user.getPassword()

        )) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }


        // Generate JWT

        String token =
                jwtService.generateToken(

                        user.getEmail(),

                        user.getRole().name()
                );


        // Return response

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