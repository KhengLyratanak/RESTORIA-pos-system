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

        OrderResponseDto dto = new OrderResponseDto();

        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setStatus(entity.getStatus());

        if (entity.getItems() != null && !entity.getItems().isEmpty()) {

            List<OrderItemResponseDto> orderItemDtos =
                    orderItemMapper.toResponseDtoList(entity.getItems());

            dto.setItems(orderItemDtos);

            Double total = calculateTotalPrice(orderItemDtos);

            dto.setTotal(total);
        } else {
            dto.setItems(List.of());
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

    private Double calculateTotalPrice(
            List<OrderItemResponseDto> items) {

        return items.stream()
                .mapToDouble(item ->
                        item.getPurchaseAmount() * item.getUnitPrice())
                .sum();
    }
}