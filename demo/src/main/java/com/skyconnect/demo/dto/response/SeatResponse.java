package com.skyconnect.demo.dto.response;


import com.skyconnect.demo.enums.SeatStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatResponse {

    private Long id;

    private String seatNumber;

    private SeatStatus status;
}