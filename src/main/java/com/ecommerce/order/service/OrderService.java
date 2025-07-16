package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderRequestDto;
import com.ecommerce.order.dto.OrderResponseDto;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductServiceUtils productServiceUtils;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper, ProductServiceUtils productServiceUtils) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.productServiceUtils = productServiceUtils;
    }

    public OrderResponseDto create(OrderRequestDto dto) {
        return orderMapper.toDto(orderRepository.save(orderMapper.toEntity(dto)));
    }

    @Transactional
    public void delete(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order does not exist"));
        if(order.isVoided()){
            throw new IllegalStateException("Order is already voided");
        }

        // Restock non-voided items
        for (OrderItem item : order.getItems()) {
            if (!item.isVoided()) {
                productServiceUtils.restockInventory(item.getProductId(), item.getQuantity());
                item.setVoided(true); // mark item voided
            }
        }

        order.setVoided(true);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    public Page<OrderResponseDto> search(String customerId, Pageable pageable) {
        Page<Order> cards = orderRepository.findOrdersByCustomerId(customerId, pageable);
        return cards.map(orderMapper::toDto);
    }

    public OrderResponseDto get(UUID id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Order does not exist"));
        return orderMapper.toDto(order);
    }
}
