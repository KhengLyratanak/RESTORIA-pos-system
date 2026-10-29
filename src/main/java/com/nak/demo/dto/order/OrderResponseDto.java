package com.nak.demo.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonPropertyOrder({"items","status","total","id","created_at","updated_at"})
public class OrderResponseDto {
    @JsonProperty("order_id")
    private Long id ;

    private String status;

    private Double total;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("cancelled_at")
    private LocalDateTime cancelledAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("cancel_reason")
    private String cancelReason;

    @JsonProperty("items")
    private List<OrderItemResponseDto> items;
}
