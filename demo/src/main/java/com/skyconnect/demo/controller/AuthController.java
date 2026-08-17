package com.skyconnect.demo.controller;

import com.skyconnect.demo.dto.request.LoginRequest;
import com.skyconnect.demo.dto.request.RegisterRequest;
import com.skyconnect.demo.dto.response.AuthResponse;
import com.skyconnect.demo.service.AuthService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;


    // =====================================================
    // REGISTER
    // CUSTOMER + ADMIN
    // =====================================================

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(

            @Valid
            @RequestBody
            RegisterRequest request

    ) {

        AuthResponse response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(

            @Valid
            @RequestBody
            LoginRequest request

    ) {

        AuthResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }
}