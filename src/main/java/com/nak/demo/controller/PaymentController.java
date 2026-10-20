package com.nak.demo.controller;

import com.nak.demo.entity.Payment;
import com.nak.demo.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // CASH: cashier enters the amount received
    @PostMapping("/orders/{orderId}/payments/cash")
    public Map<String, Object> payCash(@PathVariable Long orderId,
                                       @RequestParam BigDecimal received) {
        Payment p = paymentService.payCash(orderId, received);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("paymentId", p.getId());
        res.put("orderId", orderId);
        res.put("method", p.getMethod());
        res.put("status", p.getStatus());
        res.put("amount", p.getAmount());
        res.put("cashReceived", p.getCashReceived());
        res.put("change", p.getCashReceived().subtract(p.getAmount()));
        return res;
    }

    // KHQR step 1: create the payment and get the QR
    @PostMapping("/orders/{orderId}/payments/khqr")
    public Map<String, Object> startKhqr(@PathVariable Long orderId) {
        Payment p = paymentService.startKhqr(orderId);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("paymentId", p.getId());
        res.put("orderId", orderId);
        res.put("status", p.getStatus());
        res.put("amount", p.getAmount());
        res.put("expiresAt", p.getExpiresAt());
        res.put("md5", p.getKhqrMd5());   // replace with the real QR string later
        return res;
    }

    // KHQR step 2: frontend polls this every 3 seconds
    @GetMapping("/payments/{paymentId}")
    public Map<String, Object> checkPayment(@PathVariable Long paymentId) {
        Payment p = paymentService.checkKhqr(paymentId);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("paymentId", p.getId());
        res.put("status", p.getStatus());
        res.put("paidAt", p.getPaidAt());
        return res;
    }

    // TESTING ONLY: pretend the customer paid. Delete when Bakong is connected.
    @PostMapping("/payments/{paymentId}/simulate-paid")
    public Map<String, Object> simulatePaid(@PathVariable Long paymentId) {
        Payment p = paymentService.simulateKhqrPaid(paymentId);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("paymentId", p.getId());
        res.put("status", p.getStatus());
        return res;
    }
}