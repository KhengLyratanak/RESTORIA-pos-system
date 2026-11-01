package com.nak.demo.dto.table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TableResponseDto {
    @JsonProperty("table_id")
    private Long id;

    private String code;

    private String name;

    /** DINE_IN or TAKE_AWAY */
    private String type;

    /** FREE, or BUSY while the table has an unpaid order */
    private String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("order_id")
    private Long orderId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("order_total")
    private BigDecimal orderTotal;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("item_count")
    private Long itemCount;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("busy_since")
    private LocalDateTime busySince;
}
