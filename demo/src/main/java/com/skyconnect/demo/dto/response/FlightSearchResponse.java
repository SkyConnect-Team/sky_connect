package com.skyconnect.demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlightSearchResponse {

    private String airlineIata;

    private String flightIata;

    private String flightNumber;

    private String departureAirport;

    private String arrivalAirport;

    private String departureTime;

    private String estimatedDeparture;

    private String actualDeparture;

    private String arrivalTime;

    private String estimatedArrival;

    private String actualArrival;

    private String status;

    private Integer departureDelay;

    private Integer arrivalDelay;
}