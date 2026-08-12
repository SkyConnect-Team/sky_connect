package com.skyconnect.demo.repository;

import com.skyconnect.demo.entity.Passenger;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerRepository
        extends JpaRepository<Passenger, Long> {

    Optional<Passenger> findByEmail(
            String email
    );

    boolean existsByEmail(
            String email
    );

    boolean existsByPassportNumber(
            String passportNumber
    );
}