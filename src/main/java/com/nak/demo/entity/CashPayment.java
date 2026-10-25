package com.nak.demo.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** One row per order paid in cash, so cash shows up in reports and audits. */
@Entity
@Table(name = "cash_payments")
public class CashPayment {

    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false, unique = true)
    private Long orderId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal received;

    @Column(name = "change_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal changeAmount;

    @Column(name = "paid_at", nullable = false)
    private Instant paidAt;

    protected CashPayment() {}

    public CashPayment(Long orderId, BigDecimal total, BigDecimal received, BigDecimal changeAmount, Instant paidAt) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.total = total;
        this.received = received;
        this.changeAmount = changeAmount;
        this.paidAt = paidAt;
    }

    public UUID getId() { return id; }
    public Long getOrderId() { return orderId; }
    public BigDecimal getTotal() { return total; }
    public BigDecimal getReceived() { return received; }
    public BigDecimal getChangeAmount() { return changeAmount; }
    public Instant getPaidAt() { return paidAt; }
}