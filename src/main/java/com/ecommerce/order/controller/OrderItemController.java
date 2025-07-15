package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderItemRequestDto;
import com.ecommerce.order.dto.OrderItemResponseDto;
import com.ecommerce.order.service.OrderItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {
    private final OrderItemService  orderItemService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public ResponseEntity<OrderItemResponseDto> addItemToOrder(@RequestBody OrderItemRequestDto dto) {
        logger.info("Add item to order {}", dto.toString());
        return ResponseEntity.ok(orderItemService.addItemToOrder(dto));
    }

    @PostMapping("/remove/{orderItemId}")
    public ResponseEntity<Void> removeItemFromOrder(@PathVariable UUID orderItemId) {
        logger.info("Removing order item with id {}", orderItemId);
        orderItemService.removeItemFromOrder(orderItemId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update/{orderItemId}")
    public ResponseEntity<OrderItemResponseDto> updateItemQuantity(@PathVariable UUID orderItemId, @RequestParam int quantity) {
        logger.info("Updating order item with id {}", orderItemId);
        return ResponseEntity.ok(orderItemService.updateItemQuantity(orderItemId, quantity));
    }

}
