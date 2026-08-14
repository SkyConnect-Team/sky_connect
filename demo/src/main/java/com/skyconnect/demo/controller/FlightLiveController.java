package com.skyconnect.demo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.skyconnect.demo.dto.response.FlightLiveResponse;
import com.skyconnect.demo.dto.response.FlightSearchResponse;
import com.skyconnect.demo.service.AirLabsService;

import com.skyconnect.demo.service.AirLabsService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@CrossOrigin
public class FlightLiveController {

    private final AirLabsService airLabsService;


    @GetMapping("/{flightId}/live")
    public ResponseEntity<FlightLiveResponse> getLiveFlight(
            @PathVariable Long flightId) {

        FlightLiveResponse response =
                airLabsService.getLiveFlight(flightId);

        return ResponseEntity.ok(response);
    }

}
