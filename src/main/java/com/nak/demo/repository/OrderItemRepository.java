package com.nak.demo.repository;

import com.nak.demo.entity.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    @Query("SELECT oi.productId, SUM(oi.quantity) FROM OrderItem oi " +
            "WHERE UPPER(oi.order.status) = UPPER(:status) " +
            "AND oi.order.createdAt >= :from AND oi.order.createdAt < :to " +
            "GROUP BY oi.productId ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopProducts(@Param("status") String status,
                                   @Param("from") LocalDateTime from,
                                   @Param("to") LocalDateTime to,
                                   Pageable limit);

    /** Total items sold; null when there are no matching rows. */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi " +
            "WHERE UPPER(oi.order.status) = UPPER(:status) " +
            "AND oi.order.createdAt >= :from AND oi.order.createdAt < :to")
    Long sumQuantity(@Param("status") String status,
                     @Param("from") LocalDateTime from,
                     @Param("to") LocalDateTime to);
}
