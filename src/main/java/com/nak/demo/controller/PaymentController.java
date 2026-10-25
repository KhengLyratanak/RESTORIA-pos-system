package com.nak.demo.controller;

import com.nak.demo.dto.request.CreateKhqrRequest;
import com.nak.demo.dto.response.PaymentResponse;
import com.nak.demo.entity.Payment;
import com.nak.demo.service.PaymentService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;   // the interface, injected by Spring

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/khqr")
    public PaymentResponse create(@RequestBody CreateKhqrRequest request) {
        return paymentService.createKhqr(request.orderId());
    }

    @GetMapping("/{id}")
    public PaymentResponse status(@PathVariable UUID id) {
        return paymentService.getStatus(id);
    }
}
