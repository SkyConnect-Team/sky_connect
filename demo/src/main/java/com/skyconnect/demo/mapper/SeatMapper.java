package com.skyconnect.demo.mapper;

import com.skyconnect.demo.dto.response.SeatResponse;
import com.skyconnect.demo.entity.Seat;

import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    public SeatResponse toResponse(Seat seat) {

        return SeatResponse.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .status(seat.getStatus())
                .build();
    }
}
