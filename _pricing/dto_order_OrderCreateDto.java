package com.nak.demo.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class OrderCreateDto {
    @JsonProperty("items")
    @NotNull(message = "order item is required")
    @NotEmpty(message = "order item cannot be empty")
    private List<OrderItemDto> orderItems;

    /** Optional bill discount: PERCENT or AMOUNT. */
    @JsonProperty("discount_type")
    private String discountType;

    @JsonProperty("discount_value")
    @DecimalMin(value = "0", message = "discount value cannot be negative")
    private BigDecimal discountValue;
}
