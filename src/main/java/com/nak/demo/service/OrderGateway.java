package com.nak.demo.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
@Service
public interface OrderGateway {

    record PayableOrder(Long id, String number, BigDecimal amount, String currency) {}

    /** Return the order with the amount the server calculates. Throw if missing or already paid. */
    PayableOrder getPayableOrder(Long orderId);

    /** Mark the order paid. Must be idempotent. Called once the payment is verified with Bakong. */
    void markPaid(Long orderId, UUID paymentId);
}
