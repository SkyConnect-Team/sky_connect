package com.skyconnect.demo.dto.response;

import com.skyconnect.demo.enums.FlightStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FlightResponse {

    private Long id;

    private String flightNumber;

    private String airline;

    private String source;

    private String destination;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    private LocalDateTime actualDepartureTime;

    private LocalDateTime actualArrivalTime;

    private FlightStatus status;

    private Integer totalSeats;

    private Integer availableSeats;
}