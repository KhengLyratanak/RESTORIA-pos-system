package com.nak.demo.repository;

import com.nak.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    /** Unpaid orders that sit on a table: these make a table BUSY. */
    List<Order> findByStatusAndTableIdIsNotNull(String status);

    Optional<Order> findFirstByStatusAndTableId(String status, Long tableId);

    List<Order> findByStatusIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            String status, LocalDateTime from, LocalDateTime to);

    long countByStatusIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            String status, LocalDateTime from, LocalDateTime to);
}
