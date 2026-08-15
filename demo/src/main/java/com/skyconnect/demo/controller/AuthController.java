package com.skyconnect.demo.controller;

import com.skyconnect.demo.dto.request.LoginRequest;
import com.skyconnect.demo.dto.request.RegisterRequest;
import com.skyconnect.demo.dto.response.AuthResponse;
import com.skyconnect.demo.entity.User;
import com.skyconnect.demo.service.UserService;

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


    private final UserService userService;


    // =====================================================
    // REGISTER
    // =====================================================

    @PostMapping("/register")
    public ResponseEntity<User> register(

            @Valid
            @RequestBody
            RegisterRequest request

    ) {

        User user =
                userService.register(request);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
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
                userService.login(request);


        return ResponseEntity.ok(response);
    }
}