package com.nak.demo.controller;

import com.nak.demo.dto.response.CashPaymentResponse;
import com.nak.demo.service.CashPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/orders")
public class CashPaymentController {

    private final CashPaymentService cashPaymentService;

    public CashPaymentController(CashPaymentService cashPaymentService) {
        this.cashPaymentService = cashPaymentService;
    }

    @PostMapping("/{orderId}/payments/cash")
    public CashPaymentResponse payCash(@PathVariable Long orderId, @RequestParam BigDecimal received) {
        return cashPaymentService.payCash(orderId, received);
    }
}
