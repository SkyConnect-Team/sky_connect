package com.skyconnect.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyBookingResponse {

    private String bookingReference;

    private String flightNumber;

    private String source;

    private String destination;

    private String seatNumber;

    private String status;
}