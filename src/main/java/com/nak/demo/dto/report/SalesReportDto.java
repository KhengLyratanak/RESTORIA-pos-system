package com.nak.demo.dto.report;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonPropertyOrder({"from", "to", "group_by", "summary", "periods", "payment_methods", "top_products"})
public record SalesReportDto(
        LocalDate from,
        LocalDate to,
        @JsonProperty("group_by") String  groupBy,
        Summary summary,
        List<Period> periods,
        @JsonProperty("payment_methods")
        PaymentMethods paymentMethods,
        @JsonProperty("top_products")
        List<TopProduct> topProducts
){

        public record Summary(
                @JsonProperty("order_count") long orderCount,
                BigDecimal revenue,

                @JsonProperty("average_order_value")
                BigDecimal averageOrderValue,

                @JsonProperty("items_sold")
                long itemsSold,

                @JsonProperty("cancelled_orders")
                long cancelledOrder
        ){}

        public record Period(
                String period,

                @JsonProperty("order_count")
                long orderCount,

                BigDecimal revenue
        ){}

        public record  PaymentMethods(
                BigDecimal cash,
                BigDecimal khqr
        ){}

        public record TopProduct(
                @JsonProperty("product_id")
                Long productId,
                @JsonProperty("product_name")
                String productName,
                @JsonProperty("quantity_sold")
                long quantitySold

        ){}
}


