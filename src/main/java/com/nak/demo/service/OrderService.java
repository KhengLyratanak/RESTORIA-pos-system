package com.nak.demo.service;

import com.nak.demo.dto.order.OrderCreateDto;
import com.nak.demo.dto.order.OrderResponseDto;
import com.nak.demo.dto.order.OrderUpdateDto;
import com.nak.demo.entity.Order;
import com.nak.demo.entity.OrderItem;
import com.nak.demo.entity.Product;
import com.nak.demo.exception.model.ResourceNotFoundException;
import com.nak.demo.mapper.OrderMapper;
import com.nak.demo.repository.OrderRepository;
import com.nak.demo.repository.ProductRepository;
import com.nak.demo.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;   // NEW

    @Autowired
    private StockManagementService stockManagementService;

    public List<OrderResponseDto> listsOrder() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toResponseDtoList(orders);
    }

    @Transactional
    public void createOrder(OrderCreateDto payload) {

        // reserve stock for order
        stockManagementService.reserveStockforOrder(payload.getOrderItems());
        Order order = orderMapper.toEntity(payload);

        recalculateTotals(order);   // NEW: calculate and set total + grandTotal

        orderRepository.save(order);
    }

    public OrderResponseDto updateOrderStatus(Long orderId, OrderUpdateDto payload) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id :" + orderId));
        orderMapper.updateEntityDto(existingOrder, payload);
        orderRepository.save(existingOrder);

        return orderMapper.toResponseDto(existingOrder);
    }

    public void deleteOrder(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("order not found with id :" + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    private void recalculateTotals(Order order) {
        BigDecimal total = BigDecimal.ZERO;

        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Product not found with id :" + item.getProductId()));

                BigDecimal price = toBigDecimal(product.getPrice());
                total = total.add(price.multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        order.setGrandTotal(total);
        order.setTotal(total.doubleValue());
        order.setTotalAmount(total);
        order.setTotalPrice(total);
    }

    // Works whether getPrice() returns BigDecimal, Double, or double
    private BigDecimal toBigDecimal(Number n) {
        if (n == null) {
            throw new IllegalStateException("Product price is missing");
        }
        return (n instanceof BigDecimal) ? (BigDecimal) n : BigDecimal.valueOf(n.doubleValue());
    }
}