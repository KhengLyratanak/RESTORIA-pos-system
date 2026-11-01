package com.nak.demo.service;

import com.nak.demo.common.enums.DiscountType;
import com.nak.demo.entity.Order;
import com.nak.demo.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Works out the bill of an order:
 *
 *   subtotal        = sum of (unit price x quantity)
 *   - discount      = PERCENT of subtotal, or a fixed AMOUNT (never more than subtotal)
 *   + service       = service rate % of (subtotal - discount)
 *   + VAT           = VAT rate % of (subtotal - discount + service)
 *   = total
 *
 * Every amount is rounded to cents. Cash, KHQR and the sales report all use the total saved here.
 */
@Service
public class OrderPricingService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public void calculate(Order order) {
        BigDecimal subtotal = calculateSubtotal(order);
        BigDecimal discount = calculateDiscount(subtotal, order.getDiscountType(), order.getDiscountValue());
        BigDecimal afterDiscount = subtotal.subtract(discount);
        BigDecimal service = percentOf(afterDiscount, order.getServiceRate());
        BigDecimal vat = percentOf(afterDiscount.add(service), order.getVatRate());
        BigDecimal total = afterDiscount.add(service).add(vat);

        order.setSubtotal(subtotal);
        order.setDiscountAmount(discount);
        order.setServiceCharge(service);
        order.setVatAmount(vat);

        order.setGrandTotal(total);
        order.setTotalAmount(total);
        order.setTotalPrice(total);
        order.setTotal(total.doubleValue());
    }

    private BigDecimal calculateSubtotal(Order order) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            BigDecimal lineTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            subtotal = subtotal.add(lineTotal);
        }
        return cents(subtotal);
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal, String type, BigDecimal value) {
        if (type == null || value == null || value.signum() <= 0) {
            return cents(BigDecimal.ZERO);
        }
        BigDecimal discount;
        if (DiscountType.PERCENT.name().equals(type)) {
            discount = percentOf(subtotal, value.min(HUNDRED));
        } else {
            discount = value;
        }
        return cents(discount.min(subtotal));
    }

    private BigDecimal percentOf(BigDecimal amount, BigDecimal rate) {
        if (rate == null || rate.signum() <= 0) {
            return cents(BigDecimal.ZERO);
        }
        return cents(amount.multiply(rate).divide(HUNDRED, 4, RoundingMode.HALF_UP));
    }

    private BigDecimal cents(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
