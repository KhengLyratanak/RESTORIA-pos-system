
package com.nak.demo.repository;

import com.nak.demo.entity.CashPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface CashPaymentRepository extends JpaRepository<CashPayment, UUID> {

    boolean existsByOrderId(Long orderId);

    @Query("SELECT SUM(c.total) FROM CashPayment c WHERE c.paidAt >= :from AND c.paidAt < :to")
    BigDecimal sumTotalPaidBetween(@Param("from") Instant from, @Param("to") Instant to);
}
