package com.nak.demo.repository;

import com.nak.demo.entity.Stock;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock,Long> {
   //SELECT * FROM  stocks  WHERE product_id IN (1,3) ORDER BY <sorting_param>
    List<Stock> findByProductIdIn(List<Long> productIds, Sort createdAt);
    Optional<Stock> findFirstByProductIdOrderByCreatedAtDesc(Long productId);
}
