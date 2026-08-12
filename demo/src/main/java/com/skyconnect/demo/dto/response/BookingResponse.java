package com.skyconnect.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BookingResponse {

    private Long id;

    private String bookingReference;

    // Passenger
    private Long passengerId;
    private String passengerName;
    private String passengerEmail;
    private String passengerPhone;

    // Flight
    private Long flightId;
    private String flightNumber;
    private String airline;
    private String source;
    private String destination;

    // Seat
    private Long seatId;
    private String seatNumber;

    private String status;

    private LocalDateTime bookedAt;
}