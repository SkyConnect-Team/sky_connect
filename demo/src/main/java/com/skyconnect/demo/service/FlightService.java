package com.skyconnect.demo.service;



import com.skyconnect.demo.entity.Flight;
import com.skyconnect.demo.entity.Seat;
import com.skyconnect.demo.enums.FlightStatus;
import com.skyconnect.demo.enums.SeatStatus;
import com.skyconnect.demo.exception.ResourceNotFoundException;
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

    @Transactional
    public Flight createFlight(Flight flight) {

        flight.setStatus(FlightStatus.SCHEDULED);

        if (flight.getTotalSeats() == null ||
                flight.getTotalSeats() <= 0) {

            throw new IllegalStateException(
                    "Total seats must be greater than zero");
        }

        flight.setAvailableSeats(flight.getTotalSeats());

        Flight savedFlight = flightRepository.save(flight);

        createSeats(savedFlight);

        return savedFlight;
    }

    private void createSeats(Flight flight) {

        int totalSeats = flight.getTotalSeats();

        for (int i = 1; i <= totalSeats; i++) {

            String seatNumber = generateSeatNumber(i);

            Seat seat = Seat.builder()
                    .flight(flight)
                    .seatNumber(seatNumber)
                    .status(SeatStatus.AVAILABLE)
                    .build();

            seatRepository.save(seat);
        }
    }

    private String generateSeatNumber(int number) {

        int row = ((number - 1) / 6) + 1;

        int position = (number - 1) % 6;

        char column = (char) ('A' + position);

        return row + String.valueOf(column);
    }

    public List<Flight> getAllFlights() {

        return flightRepository.findAll();
    }

    public Flight getFlight(Long id) {

        return flightRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Flight not found"));
    }

    public List<Flight> searchFlights(
            String source,
            String destination) {

        return flightRepository
                .findBySourceIgnoreCaseAndDestinationIgnoreCase(
                        source,
                        destination
                );
    }

    public Flight updateFlight(Long id, Flight updatedFlight) {

        Flight flight = getFlight(id);

        flight.setAirline(updatedFlight.getAirline());
        flight.setSource(updatedFlight.getSource());
        flight.setDestination(updatedFlight.getDestination());
        flight.setDepartureTime(updatedFlight.getDepartureTime());
        flight.setArrivalTime(updatedFlight.getArrivalTime());
        flight.setStatus(updatedFlight.getStatus());

        return flightRepository.save(flight);
    }

    public void deleteFlight(Long id) {

        Flight flight = getFlight(id);

        flightRepository.delete(flight);
    }
}