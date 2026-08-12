package com.skyconnect.demo.service;

import com.skyconnect.demo.dto.response.SeatResponse;
import com.skyconnect.demo.mapper.SeatMapper;
import com.skyconnect.demo.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    private final SeatMapper seatMapper;


    public List<SeatResponse> getSeatsByFlight(
            Long flightId) {

        return seatRepository
                .findByFlightId(flightId)
                .stream()
                .map(seatMapper::toResponse)
                .toList();
    }
}