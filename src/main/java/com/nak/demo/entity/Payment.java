package com.nak.demo.entity;

import com.nak.demo.common.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "qr_string", nullable = false, columnDefinition = "text")
    private String qrString;

    @Column(nullable = false, unique = true, length = 32)
    private String md5;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "last_checked_at")
    private Instant lastCheckedAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "bakong_hash")
    private String bakongHash;

    protected Payment() {}

    public Payment(Long orderId, BigDecimal amount, String currency, String qrString, String md5,
                   Instant createdAt, Instant expiresAt) {
        this.id = UUID.randomUUID();
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.qrString = qrString;
        this.md5 = md5;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public UUID getId() { return id; }
    public Long getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public PaymentStatus getStatus() { return status; }
    public String getQrString() { return qrString; }
    public String getMd5() { return md5; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getLastCheckedAt() { return lastCheckedAt; }
    public Instant getPaidAt() { return paidAt; }
    public String getBakongHash() { return bakongHash; }

    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setLastCheckedAt(Instant t) { this.lastCheckedAt = t; }
    public void setPaidAt(Instant t) { this.paidAt = t; }
    public void setBakongHash(String h) { this.bakongHash = h; }
}