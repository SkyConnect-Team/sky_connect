
        package com.skyconnect.demo.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

import com.skyconnect.demo.dto.request.PaymentRequest;
import com.skyconnect.demo.dto.response.PaymentOrderResponse;
import com.skyconnect.demo.entity.Booking;
import com.skyconnect.demo.entity.Payment;
import com.skyconnect.demo.enums.BookingStatus;
import com.skyconnect.demo.enums.PaymentStatus;
import com.skyconnect.demo.repository.BookingRepository;
import com.skyconnect.demo.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;

    private final PaymentRepository paymentRepository;

    private final BookingService bookingService;


    @Value("${razorpay.key.id}")
    private String razorpayKeyId;


    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;


    // =====================================================
    // CREATE RAZORPAY ORDER
    // =====================================================

    @Transactional
    public PaymentOrderResponse createPaymentOrder(
            PaymentRequest request
    ) throws Exception {

        // -------------------------------------------------
        // 1. Find booking
        // -------------------------------------------------

        Booking booking =
                bookingRepository.findById(
                        request.getBookingId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Booking not found with id: "
                                        + request.getBookingId()
                        )
                );


        // -------------------------------------------------
        // 2. Booking must be pending payment
        // -------------------------------------------------

        if (booking.getStatus()
                != BookingStatus.PENDING_PAYMENT) {

            throw new RuntimeException(
                    "Payment cannot be created for this booking. "
                            + "Current status: "
                            + booking.getStatus()
            );
        }


        // -------------------------------------------------
        // 3. Get amount from database
        // -------------------------------------------------

        BigDecimal amount =
                booking.getTotalAmount();


        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Invalid booking amount"
            );
        }


        // -------------------------------------------------
        // 4. Check existing payment
        // -------------------------------------------------

        Payment existingPayment =
                paymentRepository
                        .findByBookingId(
                                booking.getId()
                        )
                        .orElse(null);


        if (existingPayment != null &&
                existingPayment.getRazorpayOrderId() != null &&
                existingPayment.getStatus()
                        == PaymentStatus.CREATED) {

            return PaymentOrderResponse.builder()

                    .paymentId(
                            existingPayment.getId()
                    )

                    .bookingId(
                            booking.getId()
                    )

                    .bookingReference(
                            booking.getBookingReference()
                    )

                    .amount(
                            existingPayment.getAmount()
                    )

                    .currency(
                            existingPayment.getCurrency()
                    )

                    .razorpayOrderId(
                            existingPayment
                                    .getRazorpayOrderId()
                    )

                    .razorpayKeyId(
                            razorpayKeyId
                    )

                    .status(
                            existingPayment
                                    .getStatus()
                                    .name()
                    )

                    .build();
        }


        // -------------------------------------------------
        // 5. Convert amount ₹ -> paise
        // -------------------------------------------------

        long amountInPaise =
                amount
                        .multiply(
                                BigDecimal.valueOf(100)
                        )
                        .longValueExact();


        // -------------------------------------------------
        // 6. Create Razorpay client
        // -------------------------------------------------

        RazorpayClient razorpayClient =
                new RazorpayClient(
                        razorpayKeyId,
                        razorpayKeySecret
                );


        // -------------------------------------------------
        // 7. Razorpay order options
        // -------------------------------------------------

        JSONObject options =
                new JSONObject();


        options.put(
                "amount",
                amountInPaise
        );


        options.put(
                "currency",
                "INR"
        );


        options.put(
                "receipt",
                booking.getBookingReference()
        );


        options.put(
                "payment_capture",
                1
        );


        // -------------------------------------------------
        // 8. Create Razorpay order
        // -------------------------------------------------

        Order order =
                razorpayClient.orders.create(
                        options
                );


        String razorpayOrderId =
                order.get("id");


        // -------------------------------------------------
        // 9. Create Payment entity
        // -------------------------------------------------

        Payment payment =
                Payment.builder()

                        .booking(
                                booking
                        )

                        .amount(
                                amount
                        )

                        .currency(
                                "INR"
                        )

                        .razorpayOrderId(
                                razorpayOrderId
                        )

                        .status(
                                PaymentStatus.CREATED
                        )

                        .createdAt(
                                LocalDateTime.now()
                        )

                        .build();


        // -------------------------------------------------
        // 10. Save payment
        // -------------------------------------------------

        Payment savedPayment =
                paymentRepository.save(
                        payment
                );


        // -------------------------------------------------
        // 11. Return payment order response
        // -------------------------------------------------

        return PaymentOrderResponse.builder()

                .paymentId(
                        savedPayment.getId()
                )

                .bookingId(
                        booking.getId()
                )

                .bookingReference(
                        booking.getBookingReference()
                )

                .amount(
                        amount
                )

                .currency(
                        "INR"
                )

                .razorpayOrderId(
                        razorpayOrderId
                )

                .razorpayKeyId(
                        razorpayKeyId
                )

                .status(
                        PaymentStatus.CREATED.name()
                )

                .build();
    }


    // =====================================================
    // VERIFY PAYMENT
    // =====================================================

    @Transactional
    public Payment verifyPayment(
            String razorpayPaymentId,
            String razorpayOrderId,
            String razorpaySignature
    ) throws Exception {


        // -------------------------------------------------
        // 1. Find payment using Razorpay order ID
        // -------------------------------------------------

        Payment payment =
                paymentRepository
                        .findByRazorpayOrderId(
                                razorpayOrderId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment order not found"
                                )
                        );


        // -------------------------------------------------
        // 2. Prevent duplicate verification
        // -------------------------------------------------

        if (payment.getStatus()
                == PaymentStatus.SUCCESS) {

            return payment;
        }


        // -------------------------------------------------
        // 3. Verify Razorpay signature
        // -------------------------------------------------

        JSONObject options =
                new JSONObject();


        options.put(
                "razorpay_order_id",
                payment.getRazorpayOrderId()
        );


        options.put(
                "razorpay_payment_id",
                razorpayPaymentId
        );


        options.put(
                "razorpay_signature",
                razorpaySignature
        );


        boolean verified =
                Utils.verifyPaymentSignature(
                        options,
                        razorpayKeySecret
                );


        // -------------------------------------------------
        // 4. Payment verification failed
        // -------------------------------------------------

        if (!verified) {

            payment.setStatus(
                    PaymentStatus.FAILED
            );

            paymentRepository.save(
                    payment
            );

            throw new RuntimeException(
                    "Payment signature verification failed"
            );
        }


        // -------------------------------------------------
        // 5. Payment successful
        // -------------------------------------------------

        payment.setRazorpayPaymentId(
                razorpayPaymentId
        );


        payment.setRazorpaySignature(
                razorpaySignature
        );


        payment.setStatus(
                PaymentStatus.SUCCESS
        );


        payment.setPaidAt(
                LocalDateTime.now()
        );


        Payment savedPayment =
                paymentRepository.save(
                        payment
                );


        // -------------------------------------------------
        // 6. Confirm booking
        //
        // This also sends confirmation email.
        // -------------------------------------------------

        Booking booking =
                payment.getBooking();


        if (booking.getStatus()
                == BookingStatus.PENDING_PAYMENT) {

            bookingService.confirmBookingAfterPayment(
                    booking.getId()
            );
        }


        // -------------------------------------------------
        // 7. Return payment
        // -------------------------------------------------

        return savedPayment;
    }
}

