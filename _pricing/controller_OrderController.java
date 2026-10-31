package com.nak.demo.controller;

import com.nak.demo.dto.order.CancelOrderDto;
import com.nak.demo.service.OrderService;
import com.nak.demo.dto.base.Response;
import com.nak.demo.dto.order.OrderCreateDto;
import com.nak.demo.dto.order.OrderDiscountDto;
import com.nak.demo.dto.order.OrderResponseDto;
import com.nak.demo.dto.order.OrderUpdateDto;
import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Response> placeOrder(@Valid @RequestBody OrderCreateDto payload){
         orderService.createOrder(payload);

         return ResponseEntity.status(HttpStatus.CREATED)
                 .body(Response.success("201","sucess","successfully created order"));
    }

    @PatchMapping("/{id}/discount")
    public ResponseEntity<Response> applyDiscount(@PathVariable("id") Long orderId,
                                                  @Valid @RequestBody OrderDiscountDto payload) {
        OrderResponseDto order = orderService.applyDiscount(orderId, payload);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200", "success", "successfully updated discount", order));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Response> cancelOrder(@PathVariable("id") Long orderId,
                                                @Valid @RequestBody(required = false)CancelOrderDto payload ){
        String reason = payload == null ? null : payload.getReason();
        OrderResponseDto cancelled = orderService.cancelOrder(orderId,reason);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully cancelled order"));
    }

    @GetMapping
    public ResponseEntity<Response> listOrders(){
        List<OrderResponseDto> orders = orderService.listsOrder();
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully retrieved order",orders));
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Response> updateOrderStatus(@PathVariable ("id") Long orderId, @RequestBody OrderUpdateDto payload){
      OrderResponseDto updatedOrder = orderService.updateOrderStatus(orderId,payload);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully updated order",updatedOrder));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteOrder(@PathVariable ("id") Long orderId){
         orderService.deleteOrder(orderId);
         return ResponseEntity.status(HttpStatus.OK)
                 .body(Response.success("200","success","successfully deleted order" +orderId));
    }
}
