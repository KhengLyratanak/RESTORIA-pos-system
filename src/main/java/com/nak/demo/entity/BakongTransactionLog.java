package com.nak.demo.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Audit trail of each Bakong transaction we accepted. The unique Bakong hash guarantees that one real
 * bank transaction can only ever settle one payment.
 */
@Entity
@Table(name = "bakong_transactions")
public class BakongTransactionLog {

    @Id
    private UUID id;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(nullable = false, length = 32)
    private String md5;

    @Column(name = "bakong_hash", unique = true)
    private String bakongHash;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "from_account_id")
    private String fromAccountId;

    @Column(name = "to_account_id")
    private String toAccountId;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    protected BakongTransactionLog() {}

    public BakongTransactionLog(UUID paymentId, String md5, String bakongHash, BigDecimal amount, String currency,
                                String fromAccountId, String toAccountId, Instant recordedAt) {
        this.id = UUID.randomUUID();
        this.paymentId = paymentId;
        this.md5 = md5;
        this.bakongHash = bakongHash;
        this.amount = amount;
        this.currency = currency;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.recordedAt = recordedAt;
    }

    public UUID getId() { return id; }
    public UUID getPaymentId() { return paymentId; }
    public String getMd5() { return md5; }
    public String getBakongHash() { return bakongHash; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getFromAccountId() { return fromAccountId; }
    public String getToAccountId() { return toAccountId; }
    public Instant getRecordedAt() { return recordedAt; }
}
