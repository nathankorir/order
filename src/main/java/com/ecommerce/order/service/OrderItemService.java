package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderItemRequestDto;
import com.ecommerce.order.dto.ProductResponseDto;
import com.ecommerce.order.exception.OrderException;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.repository.OrderItemRepository;
import com.ecommerce.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class OrderItemService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductServiceUtils productServiceUtils;


    public OrderItemService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductServiceUtils productServiceUtils) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productServiceUtils = productServiceUtils;
    }

    @Transactional
    public void addItemToOrder(OrderItemRequestDto dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new NoSuchElementException("Order does not exist"));
        ProductResponseDto product = productServiceUtils.getProduct(dto.getProductId());

        if (product.getQuantity() < dto.getQuantity()) {
            throw new OrderException("Insufficient stock for product: " + product.getName());
        }

        productServiceUtils.dispenseInventory(dto.getProductId(), dto.getQuantity()); // ⬅️ Extracted method

        BigDecimal price = product.getPrice();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(dto.getQuantity()));

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setPrice(price);
        item.setQuantity(dto.getQuantity());
        item.setSubtotal(subtotal);
        item.setVoided(false);

        orderItemRepository.save(item);

        order.getItems().add(item);
        order.setTotalAmount(order.getTotalAmount().add(subtotal));
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
    }

    public void removeItemFromOrder(UUID orderItemId) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NoSuchElementException("Order item does not exist"));

        if (item.isVoided()) {
            throw new OrderException("Item is already removed from the order");
        }

        item.setVoided(true);
        orderItemRepository.save(item);

        productServiceUtils.restockInventory(item.getProductId(), item.getQuantity()); // ⬅️ Extracted method

        Order order = item.getOrder();
        BigDecimal newTotal = order.getTotalAmount().subtract(item.getSubtotal());
        order.setTotalAmount(newTotal.max(BigDecimal.ZERO));
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
    }

    @Transactional
    public void updateItemQuantity(UUID orderItemId, int newQuantity) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NoSuchElementException("Order item does not exist"));

        if (item.isVoided()) {
            throw new OrderException("Cannot update a removed item.");
        }

        ProductResponseDto product = productServiceUtils.getProduct(item.getProductId());

        if (product.getQuantity() + item.getQuantity() < newQuantity) {
            throw new OrderException("Insufficient stock to update quantity for product: " + product.getName());
        }

        // Adjust inventory (restock old quantity, dispense new one)
        productServiceUtils.restockInventory(item.getProductId(), item.getQuantity());
        productServiceUtils.dispenseInventory(item.getProductId(), newQuantity);

        BigDecimal oldSubtotal = item.getSubtotal();
        BigDecimal newSubtotal = product.getPrice().multiply(BigDecimal.valueOf(newQuantity));

        item.setQuantity(newQuantity);
        item.setSubtotal(newSubtotal);
        item.setPrice(product.getPrice());

        orderItemRepository.save(item);

        Order order = item.getOrder();
        order.setTotalAmount(order.getTotalAmount().subtract(oldSubtotal).add(newSubtotal));
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
    }

}
