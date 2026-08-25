package com.skyconnect.demo.controller;

import com.skyconnect.demo.service.TicketService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;


    // =====================================================
    // GENERATE / DOWNLOAD E-TICKET
    // =====================================================

    @GetMapping("/{bookingId}/ticket")
    public ResponseEntity<byte[]> generateTicket(
            @PathVariable Long bookingId
    ) {

        byte[] pdf =
                ticketService.generateTicket(
                        bookingId
                );


        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=SkyConnect-E-Ticket-"
                                + bookingId
                                + ".pdf"
                )

                .contentType(
                        MediaType.APPLICATION_PDF
                )

                .body(pdf);
    }
}