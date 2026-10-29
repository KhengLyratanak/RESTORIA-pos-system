package com.nak.demo.service;

import com.nak.demo.entity.OrderItem;
import com.nak.demo.entity.Stock;
import com.nak.demo.repository.ProductRepository;
import com.nak.demo.repository.StockRepository;
import com.nak.demo.dto.order.OrderItemDto;
import com.nak.demo.exception.model.UnprocessableEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockManagementService {
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;
    public void reserveStockforOrder(List<OrderItemDto> orderItemDtos){
        //map for product ids
        List<Long> productIds = orderItemDtos.stream()
                .map(item -> item.getProductId())
                .toList();

        //get stocks in productids
        List<Stock> stocks = stockRepository.findByProductIdIn(productIds,
                Sort.by(Sort.Direction.ASC,"createdAt"));

        //map for required qty of productIds
        Map<Long,Long> requiredQuantities = orderItemDtos.stream()
                .collect(Collectors.toMap(OrderItemDto::getProductId,OrderItemDto::getAmount));
        //deduct stock for each product
        for (Long productId : requiredQuantities.keySet()){
            //quantity to deduct
            long remain = requiredQuantities.get(productId);

            //filter stock by productId
            List<Stock> stockByProduct = stocks.stream()
                    .filter(stock -> stock.getProduct().getId().equals(productId))
                    .toList();

            //calculate and compare qty
            for (Stock stock : stockByProduct){
                if (remain <= 0) break;

                long available = stock.getQuantity();

                if (available >= remain){
                    stock.setQuantity(available - remain);
                    remain = 0;
                }else {
                    stock.setQuantity(0L);
                    remain -= available;
                }
            }
            if (remain>0) {
                throw new UnprocessableEntityException("Not enough stock for product id : " +productId);
            }
        }
        stockRepository.saveAll(stocks);
    }
    public void returnStockForOrder(List<OrderItem> orderItems){
        if (orderItems == null) return;

        Map<Long,Long> quantities = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getProductId,
                        Collectors.summingLong(OrderItem::getQuantity)));

        for (Map.Entry<Long,Long> entry: quantities.entrySet()){
            Long productId = entry.getKey();
            long qty = entry.getValue();
            Stock stock = stockRepository.findFirstByProductIdOrderByCreatedAtDesc(productId)
                    .orElseGet(() -> {
                        Stock fresh = new Stock();
                        fresh.setQuantity(0L);
                        fresh.setProduct(productRepository.getReferenceById(productId));
                        return fresh;
                    });
            long current = stock.getQuantity() == null ? 0L :stock.getQuantity();
            stock.setQuantity(current+qty);
            stockRepository.save(stock);
        }
    }
}
