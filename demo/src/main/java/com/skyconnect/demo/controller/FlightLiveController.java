package com.skyconnect.demo.controller;


import com.skyconnect.demo.dto.response.FlightLiveResponse;
import com.skyconnect.demo.service.AirLabsService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@CrossOrigin
public class FlightLiveController {

    private final AirLabsService airLabsService;


    // =========================
    // LIVE FLIGHT
    // =========================

    @GetMapping("/{flightId}/live")
    public ResponseEntity<FlightLiveResponse> getLiveFlight(
            @PathVariable Long flightId) {

        FlightLiveResponse response =
                airLabsService.getLiveFlight(flightId);

        return ResponseEntity.ok(response);
    }


    // =========================
    // FLIGHT SCHEDULES
    // =========================
    @GetMapping(
            value = "/schedules",
            produces = "application/json"
    )
    public ResponseEntity<String> getSchedules(
            @RequestParam String depIata,
            @RequestParam String arrIata) {

        String response =
                airLabsService.getSchedules(
                        depIata,
                        arrIata
                );

        return ResponseEntity.ok(response);
    }
}