package com.nak.demo.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/** Body of PATCH /api/v1/orders/{id}/discount. Send value 0 to remove the discount. */
@Data
public class OrderDiscountDto {
    @JsonProperty("discount_type")
    @NotNull(message = "discount type is required (PERCENT or AMOUNT)")
    private String discountType;

    @JsonProperty("discount_value")
    @NotNull(message = "discount value is required")
    @DecimalMin(value = "0", message = "discount value cannot be negative")
    private BigDecimal discountValue;
}
