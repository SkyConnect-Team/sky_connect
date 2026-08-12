package com.skyconnect.demo.service;

import com.skyconnect.demo.dto.request.FlightRequest;
import com.skyconnect.demo.dto.response.FlightResponse;
import com.skyconnect.demo.entity.Flight;
import com.skyconnect.demo.entity.Seat;
import com.skyconnect.demo.enums.FlightStatus;
import com.skyconnect.demo.enums.SeatStatus;
import com.skyconnect.demo.exception.ResourceNotFoundException;
import com.skyconnect.demo.mapper.FlightMapper;
import com.skyconnect.demo.repository.FlightRepository;
import com.skyconnect.demo.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    private final SeatRepository seatRepository;

    private final FlightMapper flightMapper;


    // CREATE FLIGHT
    @Transactional
    public FlightResponse createFlight(FlightRequest request) {

        Flight flight = flightMapper.toEntity(request);

        Flight savedFlight =
                flightRepository.save(flight);

        createSeats(savedFlight);

        return flightMapper.toResponse(savedFlight);
    }


    // CREATE SEATS
    private void createSeats(Flight flight) {

        int totalSeats = flight.getTotalSeats();

        for (int i = 1; i <= totalSeats; i++) {

            String seatNumber =
                    generateSeatNumber(i);

            Seat seat = Seat.builder()
                    .flight(flight)
                    .seatNumber(seatNumber)
                    .status(SeatStatus.AVAILABLE)
                    .build();

            seatRepository.save(seat);
        }
    }


    // GENERATE SEAT NUMBER
    private String generateSeatNumber(int number) {

        int row = ((number - 1) / 6) + 1;

        int position = (number - 1) % 6;

        char column =
                (char) ('A' + position);

        return row + String.valueOf(column);
    }


    // GET ALL FLIGHTS
    public List<FlightResponse> getAllFlights() {

        return flightRepository.findAll()
                .stream()
                .map(flightMapper::toResponse)
                .toList();
    }


    // GET FLIGHT BY ID
    public FlightResponse getFlight(Long id) {

        Flight flight =
                flightRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Flight not found with id: " + id
                                )
                        );

        return flightMapper.toResponse(flight);
    }


    // SEARCH FLIGHTS
    public List<FlightResponse> searchFlights(
            String source,
            String destination) {

        return flightRepository
                .findBySourceIgnoreCaseAndDestinationIgnoreCase(
                        source,
                        destination
                )
                .stream()
                .map(flightMapper::toResponse)
                .toList();
    }


    // UPDATE FLIGHT
    public FlightResponse updateFlight(
            Long id,
            FlightRequest request) {

        Flight flight =
                flightRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Flight not found with id: " + id
                                )
                        );

        flight.setFlightNumber(
                request.getFlightNumber()
        );

        flight.setAirline(
                request.getAirline()
        );

        flight.setSource(
                request.getSource()
        );

        flight.setDestination(
                request.getDestination()
        );

        flight.setDepartureTime(
                request.getDepartureTime()
        );

        flight.setArrivalTime(
                request.getArrivalTime()
        );

        Flight updatedFlight =
                flightRepository.save(flight);

        return flightMapper.toResponse(updatedFlight);
    }


    // UPDATE FLIGHT STATUS
    public FlightResponse updateFlightStatus(
            Long id,
            FlightStatus status) {

        Flight flight =
                flightRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Flight not found with id: " + id
                                )
                        );

        flight.setStatus(status);

        Flight updatedFlight =
                flightRepository.save(flight);

        return flightMapper.toResponse(updatedFlight);
    }


    // DELETE FLIGHT
    @Transactional
    public void deleteFlight(Long id) {

        Flight flight =
                flightRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Flight not found with id: " + id
                                )
                        );

        seatRepository.deleteAll(
                seatRepository.findByFlightId(id)
        );

        flightRepository.delete(flight);
    }
}