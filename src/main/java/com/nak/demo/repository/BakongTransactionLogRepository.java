
package com.nak.demo.repository;

import com.nak.demo.entity.BakongTransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BakongTransactionLogRepository extends JpaRepository<BakongTransactionLog, UUID> {

    boolean existsByBakongHash(String bakongHash);

    boolean existsByBakongHashAndPaymentId(String bakongHash, UUID paymentId);
}

