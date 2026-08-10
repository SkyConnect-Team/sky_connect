package com.skyconnect.demo.repository;



import com.skyconnect.demo.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findBySourceIgnoreCaseAndDestinationIgnoreCase(
            String source,
            String destination
    );
}