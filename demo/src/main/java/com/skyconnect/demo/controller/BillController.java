package com.skyconnect.demo.controller;

import com.skyconnect.demo.service.BillService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;


    // =====================================================
    // DOWNLOAD BILL
    // =====================================================

    @GetMapping("/{bookingReference}/bill")
    public ResponseEntity<byte[]> downloadBill(

            @PathVariable
            String bookingReference

    ) {

        byte[] pdf =
                billService.generateBill(
                        bookingReference
                );


        String fileName =
                "SkyConnect-Bill-"
                        + bookingReference
                        + ".pdf";


        HttpHeaders headers =
                new HttpHeaders();


        headers.setContentType(
                MediaType.APPLICATION_PDF
        );


        headers.setContentDisposition(
                ContentDisposition
                        .attachment()
                        .filename(fileName)
                        .build()
        );


        headers.setContentLength(
                pdf.length
        );


        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}