package com.skyconnect.demo.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrderResponse {

    private Long paymentId;

    private Long bookingId;

    private String bookingReference;

    private BigDecimal amount;

    private String currency;

    private String razorpayOrderId;

    private String razorpayKeyId;

    private String status;
}