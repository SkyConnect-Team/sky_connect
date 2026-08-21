package com.skyconnect.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final String fromEmail = "dharshanamuthuramalingam@gmail.com";


    // =====================================================
    // GENERIC EMAIL
    // =====================================================

    public void sendEmail(
            String to,
            String subject,
            String message
    ) {

        try {

            SimpleMailMessage mailMessage =
                    new SimpleMailMessage();

            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            mailSender.send(mailMessage);

            System.out.println(
                    "Email sent successfully to: " + to
            );

        } catch (Exception e) {

            System.out.println(
                    "Failed to send email to: " + to
            );

            e.printStackTrace();
        }
    }


    // =====================================================
    // BOOKING CONFIRMATION EMAIL
    // =====================================================

    public void sendBookingConfirmationEmail(
            String customerEmail,
            String customerName,
            Long bookingId,
            String flightNumber,
            String source,
            String destination,
            String travelDate,
            int passengers,
            double amount
    ) {

        String subject =
                "SkyConnect - Booking Confirmed " + bookingId;


        String message =
                "Dear " + customerName + ",\n\n" +

                        "Your SkyConnect flight booking has been " +
                        "confirmed successfully!\n\n" +

                        "====================================\n" +
                        "       SKYCONNECT BOOKING DETAILS\n" +
                        "====================================\n\n" +

                        "Booking ID       : " + bookingId + "\n" +
                        "Flight Number    : " + flightNumber + "\n" +
                        "From             : " + source + "\n" +
                        "To               : " + destination + "\n" +
                        "Travel Date      : " + travelDate + "\n" +
                        "Passengers       : " + passengers + "\n" +
                        "Total Amount     : ₹" + amount + "\n\n" +

                        "====================================\n\n" +

                        "Your booking status is CONFIRMED.\n\n" +

                        "Thank you for choosing SkyConnect.\n\n" +

                        "Have a safe and pleasant journey!\n\n" +

                        "Regards,\n" +
                        "SkyConnect Team";


        sendEmail(
                customerEmail,
                subject,
                message
        );
    }


    // =====================================================
    // BOOKING UPDATED EMAIL
    // =====================================================

    public void sendBookingUpdateEmail(
            String customerEmail,
            String customerName,
            Long bookingId,
            String flightNumber,
            String travelDate
    ) {

        String subject =
                "SkyConnect - Booking Updated " + bookingId;


        String message =
                "Dear " + customerName + ",\n\n" +

                        "Your SkyConnect booking has been updated.\n\n" +

                        "====================================\n" +
                        "         UPDATED BOOKING\n" +
                        "====================================\n\n" +

                        "Booking ID       : " + bookingId + "\n" +
                        "Flight Number    : " + flightNumber + "\n" +
                        "Travel Date      : " + travelDate + "\n\n" +

                        "====================================\n\n" +

                        "Please check the SkyConnect application " +
                        "for your latest booking details.\n\n" +

                        "Regards,\n" +
                        "SkyConnect Team";


        sendEmail(
                customerEmail,
                subject,
                message
        );
    }


    // =====================================================
    // BOOKING CANCELLED EMAIL
    // =====================================================

    public void sendBookingCancellationEmail(
            String customerEmail,
            String customerName,
            Long bookingId,
            String flightNumber
    ) {

        String subject =
                "SkyConnect - Booking Cancelled " + bookingId;


        String message =
                "Dear " + customerName + ",\n\n" +

                        "Your SkyConnect flight booking has been cancelled.\n\n" +

                        "====================================\n" +
                        "        CANCELLED BOOKING\n" +
                        "====================================\n\n" +

                        "Booking ID       : " + bookingId + "\n" +
                        "Flight Number    : " + flightNumber + "\n\n" +

                        "====================================\n\n" +

                        "If you did not request this cancellation, " +
                        "please contact SkyConnect support.\n\n" +

                        "Regards,\n" +
                        "SkyConnect Team";


        sendEmail(
                customerEmail,
                subject,
                message
        );
    }
}