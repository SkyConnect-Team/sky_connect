package com.skyconnect.demo.service;


import com.skyconnect.demo.entity.*;
import com.skyconnect.demo.enums.BookingStatus;
import com.skyconnect.demo.enums.SeatStatus;
import com.skyconnect.demo.exception.ResourceNotFoundException;
import com.skyconnect.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;
    private final PassengerRepository passengerRepository;

    @Transactional
    public Booking bookTicket(
            Long userId,
            Long flightId,
            Long seatId,
            String passengerName,
            Integer age,
            String gender,
            String passportNumber) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"));

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Flight not found"));

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Seat not found"));

        if (!seat.getFlight().getId().equals(flightId)) {
            throw new IllegalStateException(
                    "Seat does not belong to this flight");
        }

        if (seat.getStatus() == SeatStatus.BOOKED) {
            throw new IllegalStateException(
                    "Seat is already booked");
        }

        if (flight.getAvailableSeats() <= 0) {
            throw new IllegalStateException(
                    "No seats available");
        }

        seat.setStatus(SeatStatus.BOOKED);

        flight.setAvailableSeats(
                flight.getAvailableSeats() - 1
        );

        Booking booking = Booking.builder()
                .bookingNumber(
                        "AIR-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8)
                                        .toUpperCase()
                )
                .user(user)
                .flight(flight)
                .seat(seat)
                .bookingDate(LocalDateTime.now())
                .status(BookingStatus.CONFIRMED)
                .totalAmount(4500.0)
                .build();

        Booking savedBooking =
                bookingRepository.save(booking);

        Passenger passenger = Passenger.builder()
                .booking(savedBooking)
                .name(passengerName)
                .age(age)
                .gender(gender)
                .passportNumber(passportNumber)
                .build();

        passengerRepository.save(passenger);

        seatRepository.save(seat);
        flightRepository.save(flight);

        return savedBooking;
    }

    public List<Booking> getUserBookings(Long userId) {

        return bookingRepository.findByUserId(userId);
    }

    public Booking getBooking(Long id) {

        return bookingRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found"));
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {

        Booking booking = getBooking(bookingId);

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Booking already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);

        Seat seat = booking.getSeat();
        seat.setStatus(SeatStatus.AVAILABLE);

        Flight flight = booking.getFlight();

        flight.setAvailableSeats(
                flight.getAvailableSeats() + 1
        );

        seatRepository.save(seat);
        flightRepository.save(flight);

        return bookingRepository.save(booking);
    }
}