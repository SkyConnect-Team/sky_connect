package com.skyconnect.demo.repository;



import com.skyconnect.demo.entity.Seat;
import com.skyconnect.demo.enums.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByFlightId(Long flightId);

    Optional<Seat> findByFlightIdAndSeatNumber(
            Long flightId,
            String seatNumber
    );

    List<Seat> findByFlightIdAndStatus(
            Long flightId,
            SeatStatus status
    );
}
