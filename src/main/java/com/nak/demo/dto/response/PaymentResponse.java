package com.nak.demo.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        Long orderId,
        String qr,
        BigDecimal amount,
        String currency,
        String status,
        long secondsRemaining) {}
