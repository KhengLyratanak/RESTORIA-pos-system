package com.nak.demo.service;

import com.nak.demo.dto.response.PayableOrderResponse;
import com.nak.demo.entity.Order;
import com.nak.demo.exception.model.InvalidPaymentException;
import com.nak.demo.mapper.OrderGatewayMapper;
import com.nak.demo.repository.OrderRepository;        // <-- your existing repository
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderGatewayService {

    private final OrderRepository orderRepository;
    private final OrderGatewayMapper mapper;

    public OrderGatewayService(OrderRepository orderRepository, OrderGatewayMapper mapper) {
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public PayableOrderResponse getPayableOrder(Long orderId) {
        Order order = find(orderId);
        if (mapper.isPaid(order)) throw new InvalidPaymentException("Order is already paid");
        return mapper.toPayable(order);
    }

    @Transactional
    public void markPaid(Long orderId, UUID paymentId) {
        Order order = find(orderId);
        if (mapper.isPaid(order)) return;   // idempotent
        mapper.applyPaid(order);
        orderRepository.save(order);
    }

    private Order find(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new InvalidPaymentException("Order not found"));
    }
}