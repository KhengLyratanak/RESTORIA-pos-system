package com.nak.demo.service;

import com.nak.demo.entity.Order;
import com.nak.demo.entity.Payment;
import com.nak.demo.repository.OrderRepository;
import com.nak.demo.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Transactional
    public Payment payCash(Long orderId, BigDecimal received) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        if (!"PENDING".equalsIgnoreCase(order.getStatus()))
            throw new IllegalStateException("Order already settled");

        BigDecimal total = order.getTotalAmount();      // BigDecimal

        if (received.compareTo(total) < 0)              // not "received < total"
            throw new IllegalArgumentException("Not enough cash");

        Payment p = new Payment();
        p.setOrder(order);
        p.setMethod("CASH");
        p.setAmount(total);
        p.setCashReceived(received);
        p.setStatus("SUCCESS");
        p.setPaidAt(LocalDateTime.now());
        paymentRepository.save(p);

        order.setStatus("SUCCESS");
        orderRepository.save(order);
        return p;
    }

    @Transactional
    public Payment startKhqr(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow();

        if (!"PENDING".equalsIgnoreCase(order.getStatus()))
            throw new IllegalStateException("Order already settled");

        // TEMPORARY: replace with the real Bakong KHQR SDK later
        String qr = "ORDER-" + orderId + "-" + System.currentTimeMillis();

        Payment p = new Payment();
        p.setOrder(order);
        p.setMethod("KHQR");
        p.setAmount(order.getTotalAmount());        // <-- use your real getter
        p.setKhqrMd5(DigestUtils.md5DigestAsHex(qr.getBytes(StandardCharsets.UTF_8)));
        p.setExpiresAt(LocalDateTime.now().plusMinutes(3));
        p.setStatus("PENDING");
        return paymentRepository.save(p);
    }

    @Transactional
    public Payment checkKhqr(Long paymentId) {
        Payment p = paymentRepository.findById(paymentId).orElseThrow();

        if ("PENDING".equals(p.getStatus())
                && LocalDateTime.now().isAfter(p.getExpiresAt())) {
            p.setStatus("FAILED");      // only the payment fails; order stays PENDING
        }
        return p;
    }

    // TESTING ONLY: pretend the customer paid. Remove when Bakong is connected.
    @Transactional
    public Payment simulateKhqrPaid(Long paymentId) {
        Payment p = paymentRepository.findById(paymentId).orElseThrow();

        if (!"PENDING".equals(p.getStatus()))
            throw new IllegalStateException("Payment already finished");

        p.setStatus("SUCCESS");
        p.setPaidAt(LocalDateTime.now());
        p.getOrder().setStatus("SUCCESS");
        return p;
    }
}
