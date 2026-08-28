package com.skyconnect.demo.entity;

import com.skyconnect.demo.enums.BookingStatus;

import jakarta.persistence.*;

import lombok.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "booking_reference",
            nullable = false,
            unique = true
    )
    private String bookingReference;

    // Passenger relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "passenger_id",
            nullable = false
    )
    private Passenger passenger;

    // Flight relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "flight_id",
            nullable = false
    )
    private Flight flight;

    // Seat relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "seat_id",
            nullable = false
    )
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;

    @Column(nullable = false)
    private LocalDateTime bookedAt;

    @Column(
            name = "total_amount",
            nullable = false,
            precision = 10,
            scale = 2
    )

    private BigDecimal totalAmount;
}