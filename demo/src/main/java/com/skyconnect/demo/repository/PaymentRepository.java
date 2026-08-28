
package com.skyconnect.demo.repository;

import com.skyconnect.demo.entity.Payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(
            Long bookingId
    );

    Optional<Payment> findByRazorpayOrderId(
            String razorpayOrderId
    );
}
