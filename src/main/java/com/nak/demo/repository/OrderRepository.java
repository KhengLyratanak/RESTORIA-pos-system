package com.nak.demo.repository;

import com.nak.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByStatusIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            String status, LocalDateTime from, LocalDateTime to);

    long countByStatusIgnoreCaseAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(
            String status, LocalDateTime from, LocalDateTime to);
}
