package com.skyconnect.demo.service;


import com.skyconnect.demo.entity.Seat;
import com.skyconnect.demo.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    public List<Seat> getSeatsByFlight(Long flightId) {

        return seatRepository.findByFlightId(flightId);
    }
}