package com.skyconnect.demo.service;

import com.skyconnect.demo.dto.request.BookingRequest;
import com.skyconnect.demo.dto.response.BookingResponse;
import com.skyconnect.demo.dto.response.MyBookingResponse;
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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class BookingService {


    private final BookingRepository bookingRepository;

    private final FlightRepository flightRepository;

    private final PassengerRepository passengerRepository;

    private final SeatRepository seatRepository;

    private final BookingMapper bookingMapper;


    // =====================================================
    // CREATE BOOKING
    // =====================================================

    @Transactional
    public BookingResponse createBooking(
            BookingRequest request
    ) {

        // -------------------------------------------------
        // 1. Find flight
        // -------------------------------------------------

        Flight flight =
                flightRepository.findById(
                        request.getFlightId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Flight not found with id: "
                                        + request.getFlightId()
                        )
                );


        // -------------------------------------------------
        // 2. Find passenger
        // -------------------------------------------------

        Passenger passenger =
                passengerRepository.findById(
                        request.getPassengerId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Passenger not found with id: "
                                        + request.getPassengerId()
                        )
                );


        // -------------------------------------------------
        // 3. Find seat
        // -------------------------------------------------

        Seat seat =
                seatRepository.findById(
                        request.getSeatId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Seat not found with id: "
                                        + request.getSeatId()
                        )
                );


        // -------------------------------------------------
        // 4. Check seat belongs to flight
        // -------------------------------------------------

        if (seat.getFlight() == null ||
                !seat.getFlight()
                        .getId()
                        .equals(flight.getId())) {

            throw new RuntimeException(
                    "Selected seat does not belong to this flight"
            );
        }


        // -------------------------------------------------
        // 5. Check seat availability
        // -------------------------------------------------

        if (seat.getStatus() != null &&
                seat.getStatus() != SeatStatus.AVAILABLE) {

            throw new RuntimeException(
                    "Seat is already booked"
            );
        }


        // -------------------------------------------------
        // 6. Check flight availability
        // -------------------------------------------------

        if (flight.getAvailableSeats() == null ||
                flight.getAvailableSeats() <= 0) {

            throw new RuntimeException(
                    "No available seats for this flight"
            );
        }


        // -------------------------------------------------
        // 7. Generate booking reference
        // -------------------------------------------------

        String bookingReference =
                "SKY-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase();


        // -------------------------------------------------
        // 8. Create booking
        // -------------------------------------------------

        Booking booking =
                Booking.builder()

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


        // -------------------------------------------------
        // 9. Save booking
        // -------------------------------------------------

        Booking savedBooking =
                bookingRepository.save(
                        booking
                );


        // -------------------------------------------------
        // 10. Mark seat as BOOKED
        // -------------------------------------------------

        seat.setStatus(
                SeatStatus.BOOKED
        );

        seatRepository.save(seat);


        // -------------------------------------------------
        // 11. Reduce available seats
        // -------------------------------------------------

        flight.setAvailableSeats(
                flight.getAvailableSeats() - 1
        );

        flightRepository.save(flight);


        // -------------------------------------------------
        // 12. Return booking response
        // -------------------------------------------------

        return bookingMapper.toResponse(
                savedBooking
        );
    }


    // =====================================================
    // GET MY BOOKINGS
    // =====================================================

    @Transactional
    public List<MyBookingResponse> getMyBookings() {

        // -------------------------------------------------
        // 1. Get logged-in user
        // -------------------------------------------------

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }


        // -------------------------------------------------
        // 2. Get email from JWT
        // -------------------------------------------------

        String email =
                authentication.getName();


        // -------------------------------------------------
        // 3. Find bookings using passenger email
        // -------------------------------------------------

        List<Booking> bookings =
                bookingRepository
                        .findByPassenger_Email(email);


        // -------------------------------------------------
        // 4. Convert Booking -> MyBookingResponse
        // -------------------------------------------------

        return bookings.stream()

                .map(booking -> {

                    // Load relationships while transaction
                    // is still active

                    Flight flight =
                            booking.getFlight();

                    Seat seat =
                            booking.getSeat();


                    return MyBookingResponse.builder()

                            .bookingReference(
                                    booking.getBookingReference()
                            )

                            .flightNumber(
                                    flight.getFlightNumber()
                            )

                            .source(
                                    flight.getSource()
                            )

                            .destination(
                                    flight.getDestination()
                            )

                            .seatNumber(
                                    seat.getSeatNumber()
                            )

                            .status(
                                    booking.getStatus()
                                            .name()
                            )

                            .build();
                })

                .toList();
    }


    // =====================================================
    // GET ALL BOOKINGS
    // =====================================================

    @Transactional
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

    @Transactional
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

        // -------------------------------------------------
        // 1. Find booking
        // -------------------------------------------------

        Booking booking =
                bookingRepository.findById(id)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found with id: "
                                                + id
                                )
                        );


        // -------------------------------------------------
        // 2. Check already cancelled
        // -------------------------------------------------

        if (booking.getStatus()
                == BookingStatus.CANCELLED) {

            throw new RuntimeException(
                    "Booking is already cancelled"
            );
        }


        // -------------------------------------------------
        // 3. Change booking status
        // -------------------------------------------------

        booking.setStatus(
                BookingStatus.CANCELLED
        );


        // -------------------------------------------------
        // 4. Make seat available again
        // -------------------------------------------------

        Seat seat =
                booking.getSeat();

        if (seat != null) {

            seat.setStatus(
                    SeatStatus.AVAILABLE
            );

            seatRepository.save(seat);
        }


        // -------------------------------------------------
        // 5. Increase flight available seats
        // -------------------------------------------------

        Flight flight =
                booking.getFlight();

        if (flight != null) {

            flight.setAvailableSeats(
                    flight.getAvailableSeats() + 1
            );

            flightRepository.save(flight);
        }


        // -------------------------------------------------
        // 6. Save updated booking
        // -------------------------------------------------

        Booking updatedBooking =
                bookingRepository.save(
                        booking
                );


        // -------------------------------------------------
        // 7. Return booking response
        // -------------------------------------------------

        return bookingMapper.toResponse(
                updatedBooking
        );
    }
}