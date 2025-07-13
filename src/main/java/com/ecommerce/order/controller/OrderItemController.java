package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderItemRequestDto;
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

    @PostMapping("/add")
    public ResponseEntity<Void> addItemToOrder(@RequestBody OrderItemRequestDto dto) {
        orderItemService.addItemToOrder(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove/{orderItemId}")
    public ResponseEntity<Void> removeItemFromOrder(@PathVariable UUID orderItemId) {
        orderItemService.removeItemFromOrder(orderItemId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update/{orderItemId}")
    public ResponseEntity<Void> updateItemQuantity(@PathVariable UUID orderItemId, @RequestParam int quantity) {
        orderItemService.updateItemQuantity(orderItemId, quantity);
        return ResponseEntity.ok().build();
    }

}
