package com.skyconnect.demo.mapper;

import com.skyconnect.demo.dto.request.FlightRequest;
import com.skyconnect.demo.dto.response.FlightResponse;
import com.skyconnect.demo.entity.Flight;
import com.skyconnect.demo.enums.FlightStatus;

import org.springframework.stereotype.Component;

@Component
public class FlightMapper {

    // Request DTO -> Entity
    public Flight toEntity(FlightRequest request) {

        return Flight.builder()
                .flightNumber(request.getFlightNumber())
                .airline(request.getAirline())
                .source(request.getSource())
                .destination(request.getDestination())
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .totalSeats(request.getTotalSeats())
                .availableSeats(request.getTotalSeats())
                .status(FlightStatus.SCHEDULED)
                .build();
    }

    // Entity -> Response DTO
    public FlightResponse toResponse(Flight flight) {

        return FlightResponse.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .airline(flight.getAirline())
                .source(flight.getSource())
                .destination(flight.getDestination())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .actualDepartureTime(
                        flight.getActualDepartureTime()
                )
                .actualArrivalTime(
                        flight.getActualArrivalTime()
                )
                .status(flight.getStatus())
                .totalSeats(flight.getTotalSeats())
                .availableSeats(flight.getAvailableSeats())
                .build();
    }
}