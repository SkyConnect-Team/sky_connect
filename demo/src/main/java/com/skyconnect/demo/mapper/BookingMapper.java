package com.skyconnect.demo.mapper;

import com.skyconnect.demo.dto.response.BookingResponse;
import com.skyconnect.demo.entity.Booking;

import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingResponse toResponse(
            Booking booking
    ) {

        return BookingResponse.builder()

                .id(booking.getId())

                .bookingReference(
                        booking.getBookingReference()
                )

                // Passenger
                .passengerId(
                        booking.getPassenger().getId()
                )

                .passengerName(
                        booking.getPassenger().getFirstName()
                                + " "
                                + booking.getPassenger().getLastName()
                )

                .passengerEmail(
                        booking.getPassenger().getEmail()
                )

                .passengerPhone(
                        booking.getPassenger().getPhone()
                )

                // Flight
                .flightId(
                        booking.getFlight().getId()
                )

                .flightNumber(
                        booking.getFlight().getFlightNumber()
                )

                .airline(
                        booking.getFlight().getAirline()
                )

                .source(
                        booking.getFlight().getSource()
                )

                .destination(
                        booking.getFlight().getDestination()
                )

                // Seat
                .seatId(
                        booking.getSeat().getId()
                )

                .seatNumber(
                        booking.getSeat().getSeatNumber()
                )

                // Booking
                .status(
                        booking.getStatus().name()
                )

                .bookedAt(
                        booking.getBookedAt()
                )

                .build();
    }
}