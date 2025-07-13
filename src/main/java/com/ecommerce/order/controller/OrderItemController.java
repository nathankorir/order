package com.ecommerce.order.controller;

import com.ecommerce.order.service.OrderItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {
    private final OrderItemService  orderItemService;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

}
