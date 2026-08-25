package com.skyconnect.demo.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.MultiFormatWriter;

import com.skyconnect.demo.entity.Booking;
import com.skyconnect.demo.entity.Flight;
import com.skyconnect.demo.entity.Passenger;
import com.skyconnect.demo.entity.Seat;
import com.skyconnect.demo.enums.BookingStatus;
import com.skyconnect.demo.repository.BookingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final BookingRepository bookingRepository;


    // =====================================================
    // DATE / TIME FORMATTERS
    // =====================================================

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("hh:mm a");


    // =====================================================
    // GENERATE TICKET PDF
    // =====================================================
    @Transactional(readOnly = true)
    public byte[] generateTicket(Long bookingId) {
        // -------------------------------------------------
        // 1. Get booking
        // -------------------------------------------------

        Booking booking =
                bookingRepository.findById(bookingId)

                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found with id: "
                                                + bookingId
                                )
                        );


        // -------------------------------------------------
        // 2. Security check
        // -------------------------------------------------

        checkTicketAccess(booking);


        // -------------------------------------------------
        // 3. Only confirmed booking can generate ticket
        // -------------------------------------------------

        if (booking.getStatus() != BookingStatus.CONFIRMED) {

            throw new RuntimeException(
                    "E-Ticket can be generated only for confirmed bookings"
            );
        }


        // -------------------------------------------------
        // 4. Get relationships
        // -------------------------------------------------

        Passenger passenger =
                booking.getPassenger();

        Flight flight =
                booking.getFlight();

        Seat seat =
                booking.getSeat();


        // -------------------------------------------------
        // 5. Generate PDF
        // -------------------------------------------------

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();


            Document document =
                    new Document(
                            PageSize.A4,
                            30,
                            30,
                            30,
                            30
                    );


            PdfWriter.getInstance(
                    document,
                    outputStream
            );


            document.open();


            // =================================================
            // FONTS
            // =================================================

            Font titleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            24,
                            Font.BOLD
                    );

            Font subtitleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            10,
                            Font.NORMAL
                    );

            Font sectionFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            10,
                            Font.BOLD
                    );

            Font normalFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            11,
                            Font.NORMAL
                    );

            Font boldFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            12,
                            Font.BOLD
                    );

            Font routeFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            28,
                            Font.BOLD
                    );

            Font smallFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            8,
                            Font.NORMAL
                    );


            // =================================================
            // HEADER
            // =================================================

            PdfPTable header =
                    new PdfPTable(2);

            header.setWidthPercentage(100);

            header.setWidths(
                    new float[]{70, 30}
            );


            PdfPCell logoCell =
                    new PdfPCell();

            logoCell.setBorder(
                    Rectangle.NO_BORDER
            );

            Paragraph logo =
                    new Paragraph(
                            "SKYCONNECT",
                            titleFont
                    );

            logoCell.addElement(logo);

            Paragraph airlineText =
                    new Paragraph(
                            "AIRLINE E-TICKET",
                            subtitleFont
                    );

            logoCell.addElement(
                    airlineText
            );


            header.addCell(logoCell);


            PdfPCell ticketCell =
                    new PdfPCell();

            ticketCell.setBorder(
                    Rectangle.NO_BORDER
            );

            ticketCell.setHorizontalAlignment(
                    Element.ALIGN_RIGHT
            );

            Paragraph ticketText =
                    new Paragraph(
                            "E-TICKET",
                            sectionFont
                    );

            ticketText.setAlignment(
                    Element.ALIGN_RIGHT
            );

            ticketCell.addElement(
                    ticketText
            );

            header.addCell(ticketCell);


            document.add(header);


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // ROUTE
            // =================================================

            PdfPTable routeTable =
                    new PdfPTable(3);

            routeTable.setWidthPercentage(100);

            routeTable.setWidths(
                    new float[]{35, 30, 35}
            );


            String sourceCode =
                    getAirportCode(
                            flight.getSource()
                    );

            String destinationCode =
                    getAirportCode(
                            flight.getDestination()
                    );


            PdfPCell sourceCell =
                    createNoBorderCell();

            Paragraph source =
                    new Paragraph(
                            sourceCode,
                            routeFont
                    );

            source.setAlignment(
                    Element.ALIGN_CENTER
            );

            sourceCell.addElement(
                    source
            );

            Paragraph sourceName =
                    new Paragraph(
                            flight.getSource(),
                            subtitleFont
                    );

            sourceName.setAlignment(
                    Element.ALIGN_CENTER
            );

            sourceCell.addElement(
                    sourceName
            );

            routeTable.addCell(
                    sourceCell
            );


            PdfPCell arrowCell =
                    createNoBorderCell();

            Paragraph arrow =
                    new Paragraph(
                            "---------------->",
                            boldFont
                    );

            arrow.setAlignment(
                    Element.ALIGN_CENTER
            );

            arrowCell.addElement(
                    arrow
            );

            routeTable.addCell(
                    arrowCell
            );


            PdfPCell destinationCell =
                    createNoBorderCell();

            Paragraph destination =
                    new Paragraph(
                            destinationCode,
                            routeFont
                    );

            destination.setAlignment(
                    Element.ALIGN_CENTER
            );

            destinationCell.addElement(
                    destination
            );

            Paragraph destinationName =
                    new Paragraph(
                            flight.getDestination(),
                            subtitleFont
                    );

            destinationName.setAlignment(
                    Element.ALIGN_CENTER
            );

            destinationCell.addElement(
                    destinationName
            );

            routeTable.addCell(
                    destinationCell
            );


            document.add(
                    routeTable
            );


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // PASSENGER
            // =================================================

            Paragraph passengerTitle =
                    new Paragraph(
                            "PASSENGER",
                            sectionFont
                    );

            document.add(
                    passengerTitle
            );


            String passengerName =
                    passenger.getFirstName()
                            + " "
                            + passenger.getLastName();


            Paragraph passengerParagraph =
                    new Paragraph(
                            passengerName.toUpperCase(),
                            boldFont
                    );

            document.add(
                    passengerParagraph
            );


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // FLIGHT INFORMATION
            // =================================================

            PdfPTable flightInfo =
                    new PdfPTable(4);

            flightInfo.setWidthPercentage(100);


            addInfoCell(
                    flightInfo,
                    "FLIGHT",
                    flight.getFlightNumber(),
                    sectionFont,
                    boldFont
            );


            addInfoCell(
                    flightInfo,
                    "DATE",
                    flight.getDepartureTime()
                            .format(DATE_FORMATTER),
                    sectionFont,
                    boldFont
            );


            addInfoCell(
                    flightInfo,
                    "GATE",
                    flight.getGate() != null
                            ? flight.getGate()
                            : "TBA",
                    sectionFont,
                    boldFont
            );


            addInfoCell(
                    flightInfo,
                    "SEAT",
                    seat.getSeatNumber(),
                    sectionFont,
                    boldFont
            );


            document.add(
                    flightInfo
            );


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // TIMING
            // =================================================

            PdfPTable timingTable =
                    new PdfPTable(3);

            timingTable.setWidthPercentage(100);


            addInfoCell(
                    timingTable,
                    "DEPARTURE",
                    flight.getDepartureTime()
                            .format(TIME_FORMATTER),
                    sectionFont,
                    boldFont
            );


            addInfoCell(
                    timingTable,
                    "ARRIVAL",
                    flight.getArrivalTime()
                            .format(TIME_FORMATTER),
                    sectionFont,
                    boldFont
            );


            // Check-in = 2 hours before departure
            LocalDateTime checkInTime =
                    flight.getDepartureTime()
                            .minusHours(2);


            addInfoCell(
                    timingTable,
                    "CHECK-IN",
                    checkInTime
                            .format(TIME_FORMATTER),
                    sectionFont,
                    boldFont
            );


            document.add(
                    timingTable
            );


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // BOOKING DETAILS
            // =================================================

            PdfPTable bookingTable =
                    new PdfPTable(2);

            bookingTable.setWidthPercentage(100);

            bookingTable.setWidths(
                    new float[]{50, 50}
            );


            addInfoCell(
                    bookingTable,
                    "BOOKING REFERENCE",
                    booking.getBookingReference(),
                    sectionFont,
                    boldFont
            );


            addInfoCell(
                    bookingTable,
                    "AIRLINE",
                    flight.getAirline(),
                    sectionFont,
                    boldFont
            );


            addInfoCell(
                    bookingTable,
                    "BOOKED ON",
                    booking.getBookedAt()
                            .format(
                                    DateTimeFormatter
                                            .ofPattern(
                                                    "dd MMM yyyy hh:mm a"
                                            )
                            ),
                    sectionFont,
                    boldFont
            );


            addInfoCell(
                    bookingTable,
                    "STATUS",
                    booking.getStatus()
                            .name(),
                    sectionFont,
                    boldFont
            );


            document.add(
                    bookingTable
            );


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // AMOUNT
            // =================================================

            if (booking.getTotalAmount() != null) {

                Paragraph amount =
                        new Paragraph(
                                "TOTAL AMOUNT: Rs. "
                                        + booking
                                        .getTotalAmount(),
                                boldFont
                        );

                amount.setAlignment(
                        Element.ALIGN_RIGHT
                );

                document.add(
                        amount
                );
            }


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // QR CODE + BARCODE
            // =================================================

            PdfPTable codeTable =
                    new PdfPTable(2);

            codeTable.setWidthPercentage(100);

            codeTable.setWidths(
                    new float[]{50, 50}
            );


            // QR
            try {

                byte[] qrBytes =
                        generateQRCode(
                                booking
                                        .getBookingReference()
                        );

                Image qrImage =
                        Image.getInstance(
                                qrBytes
                        );

                qrImage.scaleToFit(
                        110,
                        110
                );


                PdfPCell qrCell =
                        new PdfPCell(
                                qrImage,
                                true
                        );

                qrCell.setBorder(
                        Rectangle.NO_BORDER
                );

                qrCell.setHorizontalAlignment(
                        Element.ALIGN_CENTER
                );

                codeTable.addCell(
                        qrCell
                );

            } catch (Exception e) {

                codeTable.addCell(
                        createNoBorderCell()
                );
            }


            // Barcode
            try {

                byte[] barcodeBytes =
                        generateBarcode(
                                booking
                                        .getBookingReference()
                        );

                Image barcodeImage =
                        Image.getInstance(
                                barcodeBytes
                        );

                barcodeImage.scaleToFit(
                        220,
                        70
                );


                PdfPCell barcodeCell =
                        new PdfPCell(
                                barcodeImage,
                                true
                        );

                barcodeCell.setBorder(
                        Rectangle.NO_BORDER
                );

                barcodeCell.setHorizontalAlignment(
                        Element.ALIGN_CENTER
                );

                codeTable.addCell(
                        barcodeCell
                );

            } catch (Exception e) {

                codeTable.addCell(
                        createNoBorderCell()
                );
            }


            document.add(
                    codeTable
            );


            // =================================================
            // FOOTER
            // =================================================

            document.add(
                    new Paragraph(" ")
            );


            Paragraph line =
                    new Paragraph(
                            "------------------------------------------------------------",
                            smallFont
                    );

            line.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(
                    line
            );


            Paragraph footer =
                    new Paragraph(
                            "Please arrive at the airport at least 2 hours before departure.",
                            smallFont
                    );

            footer.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(
                    footer
            );


            Paragraph footer2 =
                    new Paragraph(
                            "This is a computer-generated e-ticket from SkyConnect.",
                            smallFont
                    );

            footer2.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(
                    footer2
            );


            document.close();


            return outputStream.toByteArray();


        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate E-Ticket PDF",
                    e
            );
        }
    }


    // =====================================================
    // SECURITY
    // =====================================================

    private void checkTicketAccess(
            Booking booking
    ) {

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


        String loggedInEmail =
                authentication.getName();


        // Admin can generate any ticket
        boolean isAdmin =
                authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority
                                                .getAuthority()
                                                .equals("ROLE_ADMIN")
                        );


        if (isAdmin) {
            return;
        }


        // Customer can generate only own ticket
        String passengerEmail =
                booking
                        .getPassenger()
                        .getEmail();


        if (!loggedInEmail.equalsIgnoreCase(
                passengerEmail
        )) {

            throw new RuntimeException(
                    "You are not authorized to generate this ticket"
            );
        }
    }


    // =====================================================
    // QR CODE
    // =====================================================

    private byte[] generateQRCode(
            String text
    ) throws Exception {

        BitMatrix matrix =
                new MultiFormatWriter()
                        .encode(
                                text,
                                BarcodeFormat.QR_CODE,
                                200,
                                200
                        );


        BufferedImage image =
                MatrixToImageWriter
                        .toBufferedImage(
                                matrix
                        );


        ByteArrayOutputStream output =
                new ByteArrayOutputStream();


        ImageIO.write(
                image,
                "PNG",
                output
        );


        return output.toByteArray();
    }


    // =====================================================
    // BARCODE
    // =====================================================

    private byte[] generateBarcode(
            String text
    ) throws Exception {

        BitMatrix matrix =
                new MultiFormatWriter()
                        .encode(
                                text,
                                BarcodeFormat.CODE_128,
                                500,
                                100
                        );


        BufferedImage image =
                MatrixToImageWriter
                        .toBufferedImage(
                                matrix
                        );


        ByteArrayOutputStream output =
                new ByteArrayOutputStream();


        ImageIO.write(
                image,
                "PNG",
                output
        );


        return output.toByteArray();
    }


    // =====================================================
    // TABLE CELL
    // =====================================================

    private void addInfoCell(
            PdfPTable table,
            String title,
            String value,
            Font titleFont,
            Font valueFont
    ) {

        PdfPCell cell =
                new PdfPCell();

        cell.setBorder(
                Rectangle.NO_BORDER
        );

        cell.setPadding(
                6
        );


        Paragraph titleParagraph =
                new Paragraph(
                        title,
                        titleFont
                );

        cell.addElement(
                titleParagraph
        );


        Paragraph valueParagraph =
                new Paragraph(
                        value,
                        valueFont
                );

        cell.addElement(
                valueParagraph
        );


        table.addCell(
                cell
        );
    }


    // =====================================================
    // NO BORDER CELL
    // =====================================================

    private PdfPCell createNoBorderCell() {

        PdfPCell cell =
                new PdfPCell();

        cell.setBorder(
                Rectangle.NO_BORDER
        );

        return cell;
    }


    // =====================================================
    // AIRPORT CODE
    // =====================================================

    private String getAirportCode(
            String city
    ) {

        if (city == null) {
            return "";
        }


        String value =
                city.trim()
                        .toLowerCase();


        return switch (value) {

            case "chennai" -> "MAA";

            case "delhi",
                 "new delhi" -> "DEL";

            case "mumbai" -> "BOM";

            case "bangalore",
                 "bengaluru" -> "BLR";

            case "hyderabad" -> "HYD";

            case "kolkata" -> "CCU";

            case "coimbatore" -> "CJB";

            case "madurai" -> "IXM";

            default ->
                    city.length() >= 3
                            ? city.substring(
                            0,
                            3
                    ).toUpperCase()
                            : city.toUpperCase();
        };
    }
}