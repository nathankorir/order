package com.ecommerce.order.mapper;

import com.ecommerce.order.dto.OrderItemRequestDto;
import com.ecommerce.order.dto.OrderItemResponseDto;
import com.ecommerce.order.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItem toEntity(OrderItemRequestDto dto);

    OrderItemResponseDto toDto(OrderItem orderItem);

    void updateFromDTO(OrderItemRequestDto dto, @MappingTarget OrderItem entity);
}
