package com.skyconnect.demo.controller;

import com.skyconnect.demo.dto.request.PassengerRequest;
import com.skyconnect.demo.dto.response.ApiResponse;
import com.skyconnect.demo.dto.response.PassengerResponse;

import com.skyconnect.demo.service.PassengerService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
@RequiredArgsConstructor
@CrossOrigin
public class PassengerController {

    private final PassengerService passengerService;


    // ==========================================
    // CREATE PASSENGER
    // ==========================================

    @PostMapping
    public ResponseEntity<
            ApiResponse<PassengerResponse>
            > createPassenger(
            @Valid @RequestBody PassengerRequest request
    ) {

        PassengerResponse passenger =
                passengerService.createPassenger(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Passenger created successfully",
                                passenger
                        )
                );
    }


    // ==========================================
    // GET ALL PASSENGERS
    // ==========================================

    @GetMapping
    public ResponseEntity<
            ApiResponse<List<PassengerResponse>>
            > getAllPassengers() {

        List<PassengerResponse> passengers =
                passengerService.getAllPassengers();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Passengers retrieved successfully",
                        passengers
                )
        );
    }


    // ==========================================
    // GET PASSENGER BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<
            ApiResponse<PassengerResponse>
            > getPassenger(
            @PathVariable Long id
    ) {

        PassengerResponse passenger =
                passengerService.getPassenger(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Passenger retrieved successfully",
                        passenger
                )
        );
    }


    // ==========================================
    // GET PASSENGER BY EMAIL
    // ==========================================

    @GetMapping("/email/{email}")
    public ResponseEntity<
            ApiResponse<PassengerResponse>
            > getPassengerByEmail(
            @PathVariable String email
    ) {

        PassengerResponse passenger =
                passengerService
                        .getPassengerByEmail(email);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Passenger retrieved successfully",
                        passenger
                )
        );
    }


    // ==========================================
    // UPDATE PASSENGER
    // ==========================================

    @PutMapping("/{id}")
    public ResponseEntity<
            ApiResponse<PassengerResponse>
            > updatePassenger(
            @PathVariable Long id,
            @Valid @RequestBody PassengerRequest request
    ) {

        PassengerResponse passenger =
                passengerService.updatePassenger(
                        id,
                        request
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Passenger updated successfully",
                        passenger
                )
        );
    }


    // ==========================================
    // DELETE PASSENGER
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<
            ApiResponse<Void>
            > deletePassenger(
            @PathVariable Long id
    ) {

        passengerService.deletePassenger(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Passenger deleted successfully",
                        null
                )
        );
    }
}