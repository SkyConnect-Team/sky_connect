package com.skyconnect.demo.repository;

import com.skyconnect.demo.entity.Booking;
import com.skyconnect.demo.enums.BookingStatus;

import org.springframework.data.jpa.repository.JpaRepository;

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
}