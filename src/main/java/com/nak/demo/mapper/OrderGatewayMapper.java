
package com.nak.demo.mapper;

import com.nak.demo.common.enums.OrderStatus;
import com.nak.demo.dto.response.PayableOrderResponse;
import com.nak.demo.entity.Order;
import com.nak.demo.exception.model.InvalidPaymentException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderGatewayMapper {

    private static final String CURRENCY = "USD";

    public PayableOrderResponse toPayable(Order o) {
        BigDecimal amount = o.getTotalAmount();

        if (amount == null) {
            amount = o.getGrandTotal();
        }

        if (amount == null) {
            amount = o.getTotalPrice();
        }

        if (amount == null && o.getTotal() != null) {
            amount = BigDecimal.valueOf(o.getTotal());
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentException(
                    "Order total is missing or invalid for order " + o.getId()
            );
        }

        return new PayableOrderResponse(
                o.getId(),
                "ORD-" + o.getId(),
                amount,
                CURRENCY
        );
    }

    public boolean isPaid(Order o) {
        String status = String.valueOf(o.getStatus());
        return status.equalsIgnoreCase("SUCCESS")
                || status.equalsIgnoreCase("PAID");
    }
    public boolean isCancelled(Order o){
        return OrderStatus.CANCELLED.name().equalsIgnoreCase(String.valueOf(o.getStatus()));
    }

    public void applyPaid(Order o) {
        o.setStatus(OrderStatus.SUCCESS.name());
    }
}
