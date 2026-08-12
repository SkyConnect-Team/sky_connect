package com.skyconnect.demo.repository;

import com.skyconnect.demo.entity.Seat;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository
        extends JpaRepository<Seat, Long> {

    List<Seat> findByFlightId(Long flightId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            "SELECT s FROM Seat s WHERE s.id = :id"
    )
    Optional<Seat> findByIdWithLock(
            @Param("id") Long id
    );
}