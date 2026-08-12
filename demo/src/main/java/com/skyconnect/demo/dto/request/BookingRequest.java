package com.skyconnect.demo.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {

    @NotNull(message = "Flight ID is required")
    private Long flightId;

    @NotNull(message = "Seat ID is required")
    private Long seatId;

    @NotNull(message = "Passenger ID is required")
    private Long passengerId;
}