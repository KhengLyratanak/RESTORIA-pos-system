package com.nak.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Only one row (id = 1): the money rules used by every till. */
@Entity
@Table(name = "shop_settings")
@Data
public class ShopSetting {
    public static final Long ID = 1L;

    @Id
    private Long id = ID;

    /** VAT in percent, e.g. 10 = 10 %. 0 = no VAT. */
    @Column(name = "vat_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal vatRate = BigDecimal.ZERO;

    /** Service charge in percent. 0 = no service charge. */
    @Column(name = "service_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal serviceRate = BigDecimal.ZERO;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
