package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderRequestDto;
import com.ecommerce.order.dto.OrderResponseDto;
import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> create(@RequestBody @Valid OrderRequestDto dto) {
        logger.info("Create order request {}", dto.toString());
        return ResponseEntity.ok(orderService.create(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        logger.info("Delete order request {}", id);
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public Page<OrderResponseDto> search(@RequestParam(required = false) String customerId, Pageable pageable) {
        return orderService.search(customerId, pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> get(@PathVariable UUID id) {
        logger.info("Get order request {}", id);
        OrderResponseDto order = orderService.get(id);
        return ResponseEntity.ok(order);
    }
}
