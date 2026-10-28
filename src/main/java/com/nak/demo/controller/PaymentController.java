package com.nak.demo.controller;

import com.nak.demo.dto.request.CreateKhqrRequest;
import com.nak.demo.dto.response.PaymentResponse;
import com.nak.demo.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/khqr")
    public PaymentResponse create(@RequestBody CreateKhqrRequest request) {
        return paymentService.createKhqr(request.orderId());
    }

    @GetMapping("/{id}")
    public PaymentResponse status(@PathVariable UUID id) {
        return paymentService.getStatus(id);
    }
}