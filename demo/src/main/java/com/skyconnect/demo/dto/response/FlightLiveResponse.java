package com.skyconnect.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FlightLiveResponse {

    private Long flightId;

    private String flightNumber;

    private String airline;

    private String source;

    private String destination;

    private String status;

    private String scheduledDeparture;

    private String estimatedDeparture;

    private String actualDeparture;

    private String scheduledArrival;

    private String estimatedArrival;

    private String actualArrival;

    private Integer departureDelay;

    private Integer arrivalDelay;

    private Double latitude;

    private Double longitude;

    private Double altitude;

    private Double speed;

    private Long lastUpdated;
}