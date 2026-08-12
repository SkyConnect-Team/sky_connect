package com.skyconnect.demo.controller;

import com.skyconnect.demo.dto.request.FlightRequest;
import com.skyconnect.demo.dto.response.ApiResponse;
import com.skyconnect.demo.dto.response.FlightResponse;
import com.skyconnect.demo.dto.response.SeatResponse;
import com.skyconnect.demo.enums.FlightStatus;
import com.skyconnect.demo.service.FlightService;
import com.skyconnect.demo.service.SeatService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@CrossOrigin
public class FlightController {

    private final FlightService flightService;

    private final SeatService seatService;


    // CREATE FLIGHT
    @PostMapping
    public ResponseEntity<ApiResponse<FlightResponse>>
    createFlight(
            @Valid @RequestBody FlightRequest request) {

        FlightResponse flight =
                flightService.createFlight(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Flight created successfully",
                                flight
                        )
                );
    }


    // GET ALL FLIGHTS
    @GetMapping
    public ResponseEntity<ApiResponse<List<FlightResponse>>>
    getAllFlights() {

        List<FlightResponse> flights =
                flightService.getAllFlights();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Flights retrieved successfully",
                        flights
                )
        );
    }


    // GET FLIGHT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightResponse>>
    getFlight(
            @PathVariable Long id) {

        FlightResponse flight =
                flightService.getFlight(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Flight retrieved successfully",
                        flight
                )
        );
    }


    // SEARCH FLIGHTS
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FlightResponse>>>
    searchFlights(
            @RequestParam String source,
            @RequestParam String destination) {

        List<FlightResponse> flights =
                flightService.searchFlights(
                        source,
                        destination
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Flights searched successfully",
                        flights
                )
        );
    }


    // UPDATE FLIGHT
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FlightResponse>>
    updateFlight(
            @PathVariable Long id,
            @Valid @RequestBody FlightRequest request) {

        FlightResponse flight =
                flightService.updateFlight(
                        id,
                        request
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Flight updated successfully",
                        flight
                )
        );
    }


    // UPDATE FLIGHT STATUS
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<FlightResponse>>
    updateFlightStatus(
            @PathVariable Long id,
            @RequestParam FlightStatus status) {

        FlightResponse flight =
                flightService.updateFlightStatus(
                        id,
                        status
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Flight status updated successfully",
                        flight
                )
        );
    }


    // DELETE FLIGHT
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>>
    deleteFlight(
            @PathVariable Long id) {

        flightService.deleteFlight(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Flight deleted successfully",
                        null
                )
        );
    }


    // GET SEATS
    @GetMapping("/{flightId}/seats")
    public ResponseEntity<ApiResponse<List<SeatResponse>>>
    getSeats(
            @PathVariable Long flightId) {

        List<SeatResponse> seats =
                seatService.getSeatsByFlight(
                        flightId
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Seats retrieved successfully",
                        seats
                )
        );
    }
}