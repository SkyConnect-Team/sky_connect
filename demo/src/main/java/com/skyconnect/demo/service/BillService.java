package com.skyconnect.demo.service;

import com.skyconnect.demo.entity.Booking;
import com.skyconnect.demo.enums.BookingStatus;
import com.skyconnect.demo.repository.BookingRepository;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BookingRepository bookingRepository;


    // =====================================================
    // GENERATE BILL PDF
    // =====================================================
    @Transactional

    public byte[] generateBill(
            String bookingReference
    ) {

        // -------------------------------------------------
        // Find booking
        // -------------------------------------------------

        Booking booking =
                bookingRepository
                        .findByBookingReference(
                                bookingReference
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found: "
                                                + bookingReference
                                )
                        );


        // -------------------------------------------------
        // Check booking status
        // -------------------------------------------------

        if (booking.getStatus()
                != BookingStatus.CONFIRMED) {

            throw new IllegalStateException(
                    "Bill can only be generated for a confirmed booking"
            );
        }


        // -------------------------------------------------
        // Create PDF
        // -------------------------------------------------

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();


            Document document =
                    new Document(
                            PageSize.A4,
                            40,
                            40,
                            40,
                            40
                    );


            PdfWriter.getInstance(
                    document,
                    outputStream
            );


            document.open();


            // =================================================
            // TITLE
            // =================================================

            Font companyFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            22,
                            Color.BLUE
                    );


            Paragraph companyName =
                    new Paragraph(
                            "SKYCONNECT",
                            companyFont
                    );

            companyName.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(companyName);


            Font billFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            16,
                            Color.DARK_GRAY
                    );


            Paragraph billTitle =
                    new Paragraph(
                            "BOOKING BILL",
                            billFont
                    );

            billTitle.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(billTitle);


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // BOOKING INFORMATION
            // =================================================

            PdfPTable bookingTable =
                    new PdfPTable(2);

            bookingTable.setWidthPercentage(100);

            bookingTable.setWidths(
                    new float[]{35, 65}
            );


            addRow(
                    bookingTable,
                    "Booking Reference",
                    booking.getBookingReference()
            );


            addRow(
                    bookingTable,
                    "Status",
                    booking.getStatus().name()
            );


            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(
                            "dd MMM yyyy, HH:mm"
                    );


            addRow(
                    bookingTable,
                    "Booked At",
                    booking.getBookedAt()
                            .format(formatter)
            );


            document.add(bookingTable);


            document.add(
                    new Paragraph(" ")
            );

            // =================================================
            // PASSENGER DETAILS
            // =================================================

            addSectionTitle(
                    document,
                    "PASSENGER DETAILS"
            );


            PdfPTable passengerTable =
                    new PdfPTable(2);

            passengerTable.setWidthPercentage(100);

            passengerTable.setWidths(
                    new float[]{35, 65}
            );


            addRow(
                    passengerTable,
                    "Passenger Name",
                    booking.getPassenger()
                            .getFirstName()
            );


            addRow(
                    passengerTable,
                    "Email",
                    booking.getPassenger()
                            .getEmail()
            );


            addRow(
                    passengerTable,
                    "Phone",
                    booking.getPassenger()
                            .getPhone()
            );


            document.add(passengerTable);


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // FLIGHT DETAILS
            // =================================================

            addSectionTitle(
                    document,
                    "FLIGHT DETAILS"
            );


            PdfPTable flightTable =
                    new PdfPTable(2);

            flightTable.setWidthPercentage(100);

            flightTable.setWidths(
                    new float[]{35, 65}
            );


            addRow(
                    flightTable,
                    "Flight Number",
                    booking.getFlight()
                            .getFlightNumber()
            );


            addRow(
                    flightTable,
                    "From",
                    booking.getFlight()
                            .getSource()
            );


            addRow(
                    flightTable,
                    "To",
                    booking.getFlight()
                            .getDestination()
            );


            addRow(
                    flightTable,
                    "Seat",
                    booking.getSeat()
                            .getSeatNumber()
            );


            document.add(flightTable);


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // PAYMENT DETAILS
            // =================================================

            addSectionTitle(
                    document,
                    "PAYMENT DETAILS"
            );


            PdfPTable paymentTable =
                    new PdfPTable(2);

            paymentTable.setWidthPercentage(100);

            paymentTable.setWidths(
                    new float[]{35, 65}
            );


            addRow(
                    paymentTable,
                    "Payment Status",
                    "PAID"
            );


            addRow(
                    paymentTable,
                    "Total Amount",
                    "₹ " +
                            booking.getTotalAmount()
                                    .toString()
            );


            document.add(paymentTable);


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // CONFIRMED MESSAGE
            // =================================================

            Font confirmedFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            14,
                            Color.GREEN
                    );


            Paragraph confirmed =
                    new Paragraph(
                            "BOOKING CONFIRMED",
                            confirmedFont
                    );

            confirmed.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(confirmed);


            document.add(
                    new Paragraph(" ")
            );


            // =================================================
            // FOOTER
            // =================================================

            Font footerFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            9,
                            Color.GRAY
                    );


            Paragraph footer =
                    new Paragraph(
                            "Thank you for choosing SkyConnect.",
                            footerFont
                    );

            footer.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(footer);


            Paragraph footer2 =
                    new Paragraph(
                            "This is a computer generated bill.",
                            footerFont
                    );

            footer2.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(footer2);


            document.close();


            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate bill",
                    e
            );
        }
    }


    // =====================================================
    // ADD TABLE ROW
    // =====================================================

    private void addRow(
            PdfPTable table,
            String label,
            String value
    ) {

        PdfPCell labelCell =
                new PdfPCell(
                        new Phrase(label)
                );

        labelCell.setBackgroundColor(
                new Color(230, 240, 250)
        );

        labelCell.setPadding(8);


        PdfPCell valueCell =
                new PdfPCell(
                        new Phrase(
                                value != null
                                        ? value
                                        : "-"
                        )
                );

        valueCell.setPadding(8);


        table.addCell(labelCell);

        table.addCell(valueCell);
    }


    // =====================================================
    // SECTION TITLE
    // =====================================================

    private void addSectionTitle(
            Document document,
            String title
    ) {

        Font font =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        12,
                        Color.BLUE
                );


        Paragraph paragraph =
                new Paragraph(
                        title,
                        font
                );


        document.add(paragraph);

        document.add(
                new Paragraph(" ")
        );
    }
}