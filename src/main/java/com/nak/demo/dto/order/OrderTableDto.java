package com.nak.demo.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Body of PATCH /api/v1/orders/{id}/table: move the order to another table. */
@Data
public class OrderTableDto {
    @JsonProperty("table_id")
    @NotNull(message = "table id is required")
    private Long tableId;
}
