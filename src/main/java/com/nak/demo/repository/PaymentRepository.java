package com.nak.demo.repository;

import com.nak.demo.common.enums.PaymentStatus;
import com.nak.demo.entity.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findFirstByOrderIdAndStatusAndExpiresAtAfter(Long orderId, PaymentStatus status, Instant now);

    /** Row lock so two polls (or a poll and the sweeper) cannot settle the same payment twice. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.id = :id")
    Optional<Payment> findByIdForUpdate(@Param("id") UUID id);

    @Query("select p.id from Payment p where p.status = :status")
    List<UUID> findIdsByStatus(@Param("status") PaymentStatus status);
}
