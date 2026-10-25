
package com.nak.demo.repository;

import com.nak.demo.entity.CashPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CashPaymentRepository extends JpaRepository<CashPayment, UUID> {

    boolean existsByOrderId(Long orderId);
}
