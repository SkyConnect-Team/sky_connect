package com.skyconnect.demo.repository;

import com.skyconnect.demo.entity.Booking;
import com.skyconnect.demo.enums.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(
            String bookingReference
    );

    List<Booking> findByStatus(
            BookingStatus status
    );

    List<Booking> findByFlightId(
            Long flightId
    );
    List<Booking> findByPassenger_Email(String email);
    @Query("""
    SELECT b
    FROM Booking b
    JOIN FETCH b.passenger
    JOIN FETCH b.flight
    JOIN FETCH b.seat
    WHERE b.bookingReference = :bookingReference
""")
    Optional<Booking> findBookingForBill(
            @Param("bookingReference") String bookingReference
    );
}