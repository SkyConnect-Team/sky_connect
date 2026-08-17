package com.skyconnect.demo.controller;

import com.skyconnect.demo.dto.request.BookingRequest;
import com.skyconnect.demo.dto.response.ApiResponse;
import com.skyconnect.demo.dto.response.BookingResponse;

import com.skyconnect.demo.dto.response.MyBookingResponse;
import com.skyconnect.demo.service.BookingService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin
public class BookingController {

    private final BookingService bookingService;


    // CREATE BOOKING
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>>
    createBooking(
            @Valid @RequestBody BookingRequest request
    ) {

        BookingResponse response =
                bookingService.createBooking(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                "Booking created successfully",
                                response
                        )
                );
    }


    // GET ALL BOOKINGS
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponse>>>
    getAllBookings() {

        List<BookingResponse> bookings =
                bookingService.getAllBookings();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Bookings retrieved successfully",
                        bookings
                )
        );
    }


    // GET BOOKING BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>>
    getBooking(
            @PathVariable Long id
    ) {

        BookingResponse booking =
                bookingService.getBooking(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Booking retrieved successfully",
                        booking
                )
        );
    }
    @GetMapping("/my")
    public ResponseEntity<
            ApiResponse<List<MyBookingResponse>>
            > getMyBookings() {

        List<MyBookingResponse> bookings =
                bookingService.getMyBookings();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "My bookings retrieved successfully",
                        bookings
                )
        );
    }

    // CANCEL BOOKING
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>>
    cancelBooking(
            @PathVariable Long id
    ) {

        BookingResponse booking =
                bookingService.cancelBooking(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Booking cancelled successfully",
                        booking
                )
        );
    }
}