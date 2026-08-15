package com.skyconnect.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;

    private final long expiration;


    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration
    ) {

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expiration = expiration;
    }


    // =====================================================
    // GENERATE JWT
    // =====================================================

    public String generateToken(
            String email,
            String role
    ) {

        Date now = new Date();

        Date expiry = new Date(
                now.getTime() + expiration
        );

        return Jwts.builder()

                .subject(email)

                .claim("role", role)

                .issuedAt(now)

                .expiration(expiry)

                .signWith(secretKey)

                .compact();
    }


    // =====================================================
    // EXTRACT EMAIL
    // =====================================================

    public String extractEmail(String token) {

        return extractClaims(token)
                .getSubject();
    }


    // =====================================================
    // EXTRACT ROLE
    // =====================================================

    public String extractRole(String token) {

        return extractClaims(token)
                .get("role", String.class);
    }


    // =====================================================
    // EXTRACT ALL CLAIMS
    // =====================================================

    public Claims extractAllClaims(String token) {

        return extractClaims(token);
    }


    // =====================================================
    // VALIDATE TOKEN
    // =====================================================

    public boolean isTokenValid(
            String token,
            String email
    ) {

        try {

            String tokenEmail =
                    extractEmail(token);

            return tokenEmail.equals(email)
                    && !isTokenExpired(token);

        } catch (Exception e) {

            return false;
        }
    }


    // =====================================================
    // CHECK EXPIRATION
    // =====================================================

    private boolean isTokenExpired(
            String token
    ) {

        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }


    // =====================================================
    // PARSE CLAIMS
    // =====================================================

    private Claims extractClaims(
            String token
    ) {

        return Jwts.parser()

                .verifyWith(secretKey)

                .build()

                .parseSignedClaims(token)

                .getPayload();
    }
}