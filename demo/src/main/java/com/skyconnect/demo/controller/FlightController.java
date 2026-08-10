
package com.skyconnect.demo.controller;

import java.util.List;

import com.skyconnect.demo.entity.Flight;
import com.skyconnect.demo.entity.Seat;
import com.skyconnect.demo.service.FlightService;
import com.skyconnect.demo.service.SeatService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@CrossOrigin
public class FlightController {

    private final FlightService flightService;
    private final SeatService seatService;

    // Create a new flight
    @PostMapping
    public Flight createFlight(@RequestBody Flight flight) {
        return flightService.createFlight(flight);
    }

    // Get all flights
    @GetMapping
    public List<Flight> getAllFlights() {
        return flightService.getAllFlights();
    }

    // Get flight by ID
    @GetMapping("/{id}")
    public Flight getFlight(@PathVariable Long id) {
        return flightService.getFlight(id);
    }

    // Search flights
    @GetMapping("/search")
    public List<Flight> searchFlights(
            @RequestParam String source,
            @RequestParam String destination) {

        return flightService.searchFlights(source, destination);
    }

    // Update flight
    @PutMapping("/{id}")
    public Flight updateFlight(
            @PathVariable Long id,
            @RequestBody Flight flight) {

        return flightService.updateFlight(id, flight);
    }

    // Delete flight
    @DeleteMapping("/{id}")
    public String deleteFlight(@PathVariable Long id) {

        flightService.deleteFlight(id);

        return "Flight deleted successfully";
    }

    // Get seats for a flight
    @GetMapping("/{flightId}/seats")
    public List<Seat> getSeats(@PathVariable Long flightId) {

        return seatService.getSeatsByFlight(flightId);
    }
}

