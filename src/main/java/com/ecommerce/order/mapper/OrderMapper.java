package com.ecommerce.order.mapper;

import com.ecommerce.order.dto.OrderRequestDto;
import com.ecommerce.order.dto.OrderResponseDto;
import com.ecommerce.order.model.Order;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    Order toEntity(OrderRequestDto dto);

    OrderResponseDto toDto(Order order);

    void updateFromDTO(OrderRequestDto dto, @MappingTarget Order entity);

    @AfterMapping
    default void setOrderNumber(@MappingTarget Order order) {
        if (order.getOrderNumber() == null || order.getOrderNumber().isBlank()) {
            order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8));
        }
    }
}
