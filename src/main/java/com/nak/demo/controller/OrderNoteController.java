package com.nak.demo.controller;

import com.nak.demo.dto.base.Response;
import com.nak.demo.entity.Order;
import com.nak.demo.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.nak.demo.exception.model.ResourceNotFoundException;
import com.nak.demo.exception.model.UnprocessableEntityException;

import java.util.Map;

/** Saves the cashier's note on an order (order type, "no onion", table, ...). */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderNoteController {

    private static final int MAX = 255;
    private final OrderRepository orderRepository;

    public OrderNoteController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PatchMapping("/{id}/note")
    @Transactional
    public ResponseEntity<Response> setNote(@PathVariable("id") Long id, @RequestBody(required = false) Map<String, String> body) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        String note = body == null ? null : body.get("note");
        note = note == null ? null : note.trim();
        if (note != null && note.length() > MAX) {
            throw new UnprocessableEntityException("note must be at most " + MAX + " characters");
        }
        order.setNote(note == null || note.isEmpty() ? null : note);
        orderRepository.save(order);
        return ResponseEntity.ok(Response.success("200", "success", "note saved"));
    }
}
