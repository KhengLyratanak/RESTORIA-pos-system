package com.nak.demo.common.enums;

public enum PaymentStatus {
    PENDING,    // waiting for the customer to pay
    PAID,       // verified with Bakong, order marked paid
    EXPIRED,    // QR timed out unpaid
    MISMATCH    // money arrived but amount, currency or account did not match
}
