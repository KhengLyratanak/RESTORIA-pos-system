package com.nak.demo.mapper;

import com.nak.demo.dto.order.*;
import com.nak.demo.entity.Order;
import com.nak.demo.entity.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    @Autowired
    private OrderItemMapper orderItemMapper;

    public Order toEntity(OrderCreateDto dto) {

        Order entity = new Order();
        entity.setTotal(0.0);

        List<OrderItem> orderItemEntities = dto.getOrderItems()
                .stream()
                .map(orderItemDto -> {
                    OrderItem orderItem = orderItemMapper.toEntity(orderItemDto);
                    orderItem.setOrder(entity);
                    return orderItem;
                })
                .toList();

        entity.setItems(orderItemEntities);

        return entity;
    }

    public OrderResponseDto toResponseDto(Order entity) {

        if (entity == null) {
            return null;
        }

        OrderNoteDto dto = new OrderNoteDto();
        dto.setNote(entity.getNote());

        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setStatus(entity.getStatus());
        dto.setCancelledAt(entity.getCancelledAt());
        dto.setCancelReason(entity.getCancelReason());
        dto.setTableId(entity.getTableId());
        dto.setTableName(entity.getTableName());
        
        if (entity.getItems() != null && !entity.getItems().isEmpty()) {
            List<OrderItemResponseDto> orderItemDtos =
                    orderItemMapper.toResponseDtoList(entity.getItems());

            dto.setItems(orderItemDtos);

            if (entity.getSubtotal() != null) {
                //new orders: use the bill saved by OrderPricingService
                setBill(dto, entity);
            } else {
                //old orders: no saved bill, work it out from the items like before
                Double total = calculateTotalPrice(orderItemDtos);
                entity.setTotal(total);
                dto.setTotal(total);
            }
        } else {
            dto.setItems(List.of());
            entity.setTotal(0.0);
            dto.setTotal(0.0);
        }

        return dto;
    }

    public List<OrderResponseDto> toResponseDtoList(List<Order> entities) {

        return entities.stream()
                .map(this::toResponseDto)
                .toList();
    }

    public void updateEntityDto(Order entity, OrderUpdateDto dto) {

        if (entity == null || dto == null) {
            return;
        }

        entity.setStatus(dto.getStatus());
    }

    private void setBill(OrderResponseDto dto, Order entity) {
        dto.setSubtotal(entity.getSubtotal());
        dto.setDiscountType(entity.getDiscountType());
        dto.setDiscountValue(entity.getDiscountValue());
        dto.setDiscountAmount(entity.getDiscountAmount());
        dto.setServiceRate(entity.getServiceRate());
        dto.setServiceCharge(entity.getServiceCharge());
        dto.setVatRate(entity.getVatRate());
        dto.setVatAmount(entity.getVatAmount());
        dto.setTotal(entity.getGrandTotal().doubleValue());
    }

    private Double calculateTotalPrice(
            List<OrderItemResponseDto> items) {

        return items.stream()
                .mapToDouble(item ->
                        item.getPurchaseAmount() * item.getUnitPrice())
                .sum();
    }
}