package com.ecommerce.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ecommerce.order.enums.Enum.PaymentStatus;
import com.ecommerce.order.enums.Enum.OrderStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private String id;
    private String orderNumber;
    private String customerId;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private List<OrderItemResponseDto> items;
}
