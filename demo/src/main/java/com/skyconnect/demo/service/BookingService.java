package com.skyconnect.demo.service;

import com.skyconnect.demo.dto.request.BookingRequest;
import com.skyconnect.demo.dto.response.BookingResponse;

import com.skyconnect.demo.entity.Booking;
import com.skyconnect.demo.entity.Flight;
import com.skyconnect.demo.entity.Passenger;
import com.skyconnect.demo.entity.Seat;

import com.skyconnect.demo.enums.BookingStatus;

import com.skyconnect.demo.enums.SeatStatus;
import com.skyconnect.demo.mapper.BookingMapper;

import com.skyconnect.demo.repository.BookingRepository;
import com.skyconnect.demo.repository.FlightRepository;
import com.skyconnect.demo.repository.PassengerRepository;
import com.skyconnect.demo.repository.SeatRepository;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final FlightRepository flightRepository;

    private final SeatRepository seatRepository;

    private final PassengerRepository passengerRepository;

    private final BookingMapper bookingMapper;


    // =====================================================
    // CREATE BOOKING
    // =====================================================

    @Transactional
    public BookingResponse createBooking(
            BookingRequest request
    ) {

        // 1. Find flight
        Flight flight =
                flightRepository.findById(
                        request.getFlightId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Flight not found with id: "
                                        + request.getFlightId()
                        )
                );


        // 2. Find passenger
        Passenger passenger =
                passengerRepository.findById(
                        request.getPassengerId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Passenger not found with id: "
                                        + request.getPassengerId()
                        )
                );


        // 3. Find seat
        Seat seat =
                seatRepository.findById(
                        request.getSeatId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Seat not found with id: "
                                        + request.getSeatId()
                        )
                );


        // 4. Make sure seat belongs to selected flight
        if (!seat.getFlight().getId()
                .equals(flight.getId())) {

            throw new RuntimeException(
                    "Selected seat does not belong to this flight"
            );
        }


        // 5. Check seat availability
        if (seat.getStatus() != null
                && !seat.getStatus().name().equals("AVAILABLE")) {

            throw new RuntimeException(
                    "Seat is already booked"
            );
        }


        // 6. Check flight available seats
        if (flight.getAvailableSeats() <= 0) {

            throw new RuntimeException(
                    "No available seats for this flight"
            );
        }


        // 7. Generate booking reference
        String bookingReference =
                "SKY-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase();


        // 8. Create booking
        Booking booking = Booking.builder()

                .bookingReference(
                        bookingReference
                )

                .passenger(
                        passenger
                )

                .flight(
                        flight
                )

                .seat(
                        seat
                )

                .status(
                        BookingStatus.CONFIRMED
                )

                .bookedAt(
                        LocalDateTime.now()
                )

                .build();


        // 9. Save booking
        Booking savedBooking =
                bookingRepository.save(
                        booking
                );


        // 10. Mark seat as booked
        seat.setStatus(
                SeatStatus.BOOKED
        );

        seatRepository.save(seat);


        // 11. Reduce available seats
        flight.setAvailableSeats(
                flight.getAvailableSeats() - 1
        );

        flightRepository.save(flight);


        // 12. Return response
        return bookingMapper.toResponse(
                savedBooking
        );
    }


    // =====================================================
    // GET ALL BOOKINGS
    // =====================================================

    public List<BookingResponse> getAllBookings() {

        return bookingRepository
                .findAll()
                .stream()
                .map(bookingMapper::toResponse)
                .toList();
    }


    // =====================================================
    // GET BOOKING BY ID
    // =====================================================

    public BookingResponse getBooking(
            Long id
    ) {

        Booking booking =
                bookingRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found with id: "
                                                + id
                                )
                        );

        return bookingMapper.toResponse(
                booking
        );
    }


    // =====================================================
    // CANCEL BOOKING
    // =====================================================

    @Transactional
    public BookingResponse cancelBooking(
            Long id
    ) {

        Booking booking =
                bookingRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found with id: "
                                                + id
                                )
                        );


        // Already cancelled
        if (booking.getStatus()
                == BookingStatus.CANCELLED) {

            throw new RuntimeException(
                    "Booking is already cancelled"
            );
        }


        // Change booking status
        booking.setStatus(
                BookingStatus.CANCELLED
        );


        // Make seat available again
        Seat seat =
                booking.getSeat();

        seat.setStatus(
                SeatStatus.AVAILABLE
        );

        seatRepository.save(seat);


        // Increase flight available seats
        Flight flight =
                booking.getFlight();

        flight.setAvailableSeats(
                flight.getAvailableSeats() + 1
        );

        flightRepository.save(flight);


        Booking updatedBooking =
                bookingRepository.save(
                        booking
                );


        return bookingMapper.toResponse(
                updatedBooking
        );
    }
}