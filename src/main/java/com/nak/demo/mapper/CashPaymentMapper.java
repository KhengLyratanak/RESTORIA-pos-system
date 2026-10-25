package com.nak.demo.mapper;

import com.nak.demo.dto.response.CashPaymentResponse;
import com.nak.demo.entity.CashPayment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class CashPaymentMapper {

    public CashPayment toEntity(Long orderId, BigDecimal total, BigDecimal received, Instant paidAt) {
        return new CashPayment(orderId, total, received, received.subtract(total), paidAt);
    }

    public CashPaymentResponse toResponse(CashPayment c) {
        return new CashPaymentResponse(c.getOrderId(), c.getTotal(), c.getReceived(), c.getChangeAmount(), "PAID");
    }
}

