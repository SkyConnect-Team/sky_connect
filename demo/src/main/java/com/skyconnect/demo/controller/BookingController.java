package com.skyconnect.demo.controller;



import com.skyconnect.demo.entity.Booking;
import com.skyconnect.demo.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking bookTicket(
            @RequestParam Long userId,
            @RequestParam Long flightId,
            @RequestParam Long seatId,
            @RequestParam String passengerName,
            @RequestParam Integer age,
            @RequestParam String gender,
            @RequestParam(required = false)
            String passportNumber) {

        return bookingService.bookTicket(
                userId,
                flightId,
                seatId,
                passengerName,
                age,
                gender,
                passportNumber
        );
    }

    @GetMapping("/{id}")
    public Booking getBooking(
            @PathVariable Long id) {

        return bookingService.getBooking(id);
    }

    @GetMapping("/user/{userId}")
    public List<Booking> getUserBookings(
            @PathVariable Long userId) {

        return bookingService.getUserBookings(userId);
    }

    @PutMapping("/{id}/cancel")
    public Booking cancelBooking(
            @PathVariable Long id) {

        return bookingService.cancelBooking(id);
    }
}