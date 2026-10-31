package com.nak.demo.service;

import com.nak.demo.common.enums.OrderStatus;
import com.nak.demo.common.enums.PaymentStatus;
import com.nak.demo.dto.order.OrderCreateDto;
import com.nak.demo.dto.order.OrderResponseDto;
import com.nak.demo.dto.order.OrderUpdateDto;
import com.nak.demo.entity.Order;
import com.nak.demo.entity.OrderItem;
import com.nak.demo.entity.Product;
import com.nak.demo.exception.model.ResourceNotFoundException;
import com.nak.demo.exception.model.UnprocessableEntityException;
import com.nak.demo.mapper.OrderMapper;
import com.nak.demo.repository.OrderRepository;
import com.nak.demo.repository.PaymentRepository;
import com.nak.demo.repository.ProductRepository;
import com.nak.demo.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private ProductRepository productRepository;

    @Autowired
    private StockManagementService stockManagementService;

    @Autowired
    private PaymentRepository paymentRepository;
    public List<OrderResponseDto> listsOrder() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.toResponseDtoList(orders);
    }

    @Transactional
    public OrderResponseDto cancelOrder(Long orderId, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id :" + orderId));

        String status = String.valueOf(order.getStatus()).toUpperCase();
        if (status.equals(OrderStatus.CANCELLED.name())) {
            throw new UnprocessableEntityException("order " + orderId + " is already cancelled");
        }
        if (status.equals(OrderStatus.SUCCESS.name()) || status.equals("PAID")) {
            throw new UnprocessableEntityException("order " + orderId + " is already paid and cannot be cancelled");
        }
        if (paymentRepository.existsByOrderIdAndStatus(orderId, PaymentStatus.PENDING)) {
            throw new UnprocessableEntityException(" a KHQR payment for order " + orderId + " is still in progress, try again after it expires");
        }
        stockManagementService.returnStockForOrder(order.getItems());

        order.setStatus(OrderStatus.CANCELLED.name());
        order.setCancelledAt(LocalDateTime.now());
        order.setCancelReason(reason == null || reason.isBlank() ? null : reason.trim());
        orderRepository.save(order);

        return orderMapper.toResponseDto(order);
    }

    @Transactional
    public void createOrder(OrderCreateDto payload) {

        stockManagementService.reserveStockforOrder(payload.getOrderItems());
        Order order = orderMapper.toEntity(payload);

        recalculateTotals(order);

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

    private BigDecimal toBigDecimal(Number n) {
        if (n == null) {
            throw new IllegalStateException("Product price is missing");
        }
        return (n instanceof BigDecimal) ? (BigDecimal) n : BigDecimal.valueOf(n.doubleValue());
    }
}