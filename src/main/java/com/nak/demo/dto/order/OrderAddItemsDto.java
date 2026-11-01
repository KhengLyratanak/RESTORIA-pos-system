package com.nak.demo.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/** Body of POST /api/v1/orders/{id}/items: more items for an unpaid order (the customer orders more). */
@Data
public class OrderAddItemsDto {
    @JsonProperty("items")
    @NotEmpty(message = "add at least one item")
    @Valid
    private List<OrderItemDto> orderItems;
}
