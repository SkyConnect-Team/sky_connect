package com.skyconnect.demo.controller;

import com.skyconnect.demo.dto.request.PaymentRequest;
import com.skyconnect.demo.dto.response.PaymentOrderResponse;
import com.skyconnect.demo.entity.Payment;
import com.skyconnect.demo.service.PaymentService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;


    // =====================================================
    // CREATE PAYMENT ORDER
    // =====================================================

    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse>
    createPaymentOrder(
            @Valid @RequestBody PaymentRequest request
    ) throws Exception {

        return ResponseEntity.ok(
                paymentService.createPaymentOrder(
                        request
                )
        );
    }


    // =====================================================
    // VERIFY PAYMENT
    // =====================================================

    @PostMapping("/verify")
    public ResponseEntity<Payment>
    verifyPayment(
            @RequestParam String razorpayPaymentId,

            @RequestParam String razorpayOrderId,

            @RequestParam String razorpaySignature
    ) throws Exception {

        Payment payment =
                paymentService.verifyPayment(
                        razorpayPaymentId,
                        razorpayOrderId,
                        razorpaySignature
                );

        return ResponseEntity.ok(payment);
    }
}