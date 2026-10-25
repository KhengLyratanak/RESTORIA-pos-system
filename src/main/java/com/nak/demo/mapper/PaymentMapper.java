package com.nak.demo.mapper;

import com.nak.demo.dto.response.PaymentResponse;
import com.nak.demo.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment p, long secondsRemaining) {
        return new PaymentResponse(
                p.getId(),
                p.getOrderId(),
                p.getQrString(),
                p.getAmount(),
                p.getCurrency(),
                p.getStatus().name(),
                secondsRemaining);
    }
}
