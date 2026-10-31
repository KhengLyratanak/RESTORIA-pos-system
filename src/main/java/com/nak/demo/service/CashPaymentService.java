package com.nak.demo.service;

import com.nak.demo.dto.response.CashPaymentResponse;
import com.nak.demo.entity.CashPayment;
import com.nak.demo.exception.model.InvalidPaymentException;
import com.nak.demo.mapper.CashPaymentMapper;
import com.nak.demo.repository.CashPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;

@Service
public class CashPaymentService {

    private final OrderGatewayService orders;
    private final CashPaymentRepository repository;
    private final CashPaymentMapper mapper;
    private final Clock clock;

    public CashPaymentService(OrderGatewayService orders, CashPaymentRepository repository,
                              CashPaymentMapper mapper, Clock clock) {
        this.orders = orders;
        this.repository = repository;
        this.mapper = mapper;
        this.clock = clock;
    }

    @Transactional
    public CashPaymentResponse payCash(Long orderId, BigDecimal received) {
        if (orderId == null) throw new InvalidPaymentException("orderId is required");
        if (received == null || received.signum() <= 0) throw new InvalidPaymentException("Enter the cash received");

        var order = orders.getPayableOrder(orderId);
        if (received.compareTo(order.amount()) < 0) {
            throw new InvalidPaymentException("Cash received is less than the total");
        }
        if (repository.existsByOrderId(orderId)) {
            throw new InvalidPaymentException("This order was already paid in cash");
        }

        CashPayment saved = repository.save(mapper.toEntity(orderId, order.amount(), received, clock.instant()));
        orders.markPaid(orderId, null);
        return mapper.toResponse(saved);
    }
}