package com.nak.demo.dto.response;

import java.math.BigDecimal;

public record CashPaymentResponse(
        Long orderId,
        BigDecimal total,
        BigDecimal received,
        BigDecimal change,
        String status) {}
