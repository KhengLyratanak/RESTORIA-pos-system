package com.nak.demo.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonPropertyOrder({"items","status","total","id","created_at","updated_at"})
public class OrderResponseDto {
    @JsonProperty("order_id")
    private Long id ;

    private String status;

    private Double total;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("table_id")
    private Long tableId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("table_name")
    private String tableName;

    /** Bill breakdown. Missing (null) on orders made before discounts existed. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal subtotal;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("discount_type")
    private String discountType;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("discount_value")
    private BigDecimal discountValue;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("discount_amount")
    private BigDecimal discountAmount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("service_rate")
    private BigDecimal serviceRate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("service_charge")
    private BigDecimal serviceCharge;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("vat_rate")
    private BigDecimal vatRate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("vat_amount")
    private BigDecimal vatAmount;

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
