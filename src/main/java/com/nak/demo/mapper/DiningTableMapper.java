package com.nak.demo.mapper;

import com.nak.demo.dto.table.TableResponseDto;
import com.nak.demo.entity.DiningTable;
import com.nak.demo.entity.Order;
import com.nak.demo.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class DiningTableMapper {

    /** openOrder is the table's unpaid order, or null when the table is free. */
    public TableResponseDto toResponseDto(DiningTable table, Order openOrder) {
        TableResponseDto dto = new TableResponseDto();
        dto.setId(table.getId());
        dto.setCode(table.getCode());
        dto.setName(table.getName());
        dto.setType(table.getType());

        if (openOrder == null) {
            dto.setStatus("FREE");
            return dto;
        }
        dto.setStatus("BUSY");
        dto.setOrderId(openOrder.getId());
        dto.setOrderTotal(openOrder.getGrandTotal());
        dto.setItemCount(countItems(openOrder));
        dto.setBusySince(openOrder.getCreatedAt());
        return dto;
    }

    private Long countItems(Order order) {
        long count = 0;
        for (OrderItem item : order.getItems()) {
            count += item.getQuantity();
        }
        return count;
    }
}
