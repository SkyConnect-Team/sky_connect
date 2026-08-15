package com.skyconnect.demo.controller;

import com.skyconnect.demo.dto.request.LoginRequest;
import com.skyconnect.demo.dto.response.AuthResponse;
import com.skyconnect.demo.entity.User;
import com.skyconnect.demo.service.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {


    private final UserService userService;


    // ==========================================
    // REGISTER
    // ==========================================
//
//    @PostMapping("/register")
//    public ResponseEntity<User> register(
//
//            @Valid
//            @RequestBody
//            User user
//
//    ) {
//
//        User savedUser =
//                userService.register(user);
//
//
//        // IMPORTANT:
//        // Don't return password to frontend
//
//        savedUser.setPassword(null);
//
//
//        return ResponseEntity.ok(
//                savedUser
//        );
//    }
//
//
//    // ==========================================
//    // LOGIN
//    // ==========================================
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(
//
//            @Valid
//            @RequestBody
//            LoginRequest request
//
//    ) {
//
//        AuthResponse response =
//                userService.login(request);
//
//
//        return ResponseEntity.ok(
//                response
//        );
//    }
//

    // ==========================================
    // GET USER
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(

            @PathVariable Long id

    ) {

        User user =
                userService.getUser(id);


        // Don't expose password
        user.setPassword(null);


        return ResponseEntity.ok(
                user
        );
    }
}