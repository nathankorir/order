package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderItemRequestDto;
import com.ecommerce.order.dto.ProductResponseDto;
import com.ecommerce.order.exception.OrderException;
import com.ecommerce.order.mapper.OrderItemMapper;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderItem;
import com.ecommerce.order.repository.OrderItemRepository;
import com.ecommerce.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderItemServiceTests {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductServiceUtils productServiceUtils;

    @InjectMocks
    private OrderItemService orderItemService;

    @Mock
    private OrderItemMapper orderItemMapper;

    private UUID orderId;
    private UUID productId;
    private UUID orderItemId;
    private Order order;
    private ProductResponseDto product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        orderId = UUID.randomUUID();
        productId = UUID.randomUUID();
        orderItemId = UUID.randomUUID();

        order = new Order();
        order.setId(orderId);
        order.setItems(new ArrayList<>());
        order.setTotalAmount(BigDecimal.ZERO);
        order.setVoided(false);
        order.setUpdatedAt(LocalDateTime.now());

        product = new ProductResponseDto();
        product.setId(productId);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));
        product.setQuantity(10);
    }

    @Test
    void addItemToOrder_success() {
        OrderItemRequestDto dto = new OrderItemRequestDto(orderId, productId, 2);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(productServiceUtils.getProduct(productId)).thenReturn(product);

        orderItemService.addItemToOrder(dto);

        verify(productServiceUtils).dispenseInventory(productId, 2);
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(orderRepository).save(order);
        assertEquals(1, order.getItems().size());
        assertEquals(BigDecimal.valueOf(200), order.getTotalAmount());
    }

    @Test
    void addItemToOrder_insufficientStock_shouldThrow() {
        OrderItemRequestDto dto = new OrderItemRequestDto(orderId, productId, 20);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(productServiceUtils.getProduct(productId)).thenReturn(product);

        assertThrows(OrderException.class, () -> orderItemService.addItemToOrder(dto));
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void removeItemFromOrder_success() {
        OrderItem item = new OrderItem();
        item.setId(orderItemId);
        item.setOrder(order);
        item.setProductId(productId);
        item.setQuantity(2);
        item.setSubtotal(BigDecimal.valueOf(200));
        item.setVoided(false);
        order.getItems().add(item);
        order.setTotalAmount(BigDecimal.valueOf(200));

        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(item));

        orderItemService.removeItemFromOrder(orderItemId);

        assertTrue(item.isVoided());
        assertEquals(BigDecimal.ZERO, order.getTotalAmount());
        verify(productServiceUtils).restockInventory(productId, 2);
        verify(orderItemRepository).save(item);
        verify(orderRepository).save(order);
    }

    @Test
    void removeItemFromOrder_alreadyVoided_shouldThrow() {
        OrderItem item = new OrderItem();
        item.setId(orderItemId);
        item.setOrder(order);
        item.setVoided(true);

        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(item));

        assertThrows(OrderException.class, () -> orderItemService.removeItemFromOrder(orderItemId));
        verify(orderItemRepository, never()).save(any());
    }

    @Test
    void updateItemQuantity_success() {
        OrderItem item = new OrderItem();
        item.setId(orderItemId);
        item.setOrder(order);
        item.setProductId(productId);
        item.setQuantity(2);
        item.setSubtotal(BigDecimal.valueOf(200));
        item.setVoided(false);
        item.setPrice(BigDecimal.valueOf(100));
        order.getItems().add(item);
        order.setTotalAmount(BigDecimal.valueOf(200));

        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(item));
        when(productServiceUtils.getProduct(productId)).thenReturn(product);

        orderItemService.updateItemQuantity(orderItemId, 5);

        verify(productServiceUtils).restockInventory(productId, 2);
        verify(productServiceUtils).dispenseInventory(productId, 5);
        verify(orderItemRepository).save(item);
        verify(orderRepository).save(order);

        assertEquals(5, item.getQuantity());
        assertEquals(BigDecimal.valueOf(500), item.getSubtotal());
        assertEquals(BigDecimal.valueOf(500), order.getTotalAmount());
    }

    @Test
    void updateItemQuantity_insufficientStock_shouldThrow() {
        OrderItem item = new OrderItem();
        item.setId(orderItemId);
        item.setOrder(order);
        item.setProductId(productId);
        item.setQuantity(2);
        item.setSubtotal(BigDecimal.valueOf(200));
        item.setVoided(false);

        product.setQuantity(1); // not enough to satisfy newQuantity = 5

        when(orderItemRepository.findById(orderItemId)).thenReturn(Optional.of(item));
        when(productServiceUtils.getProduct(productId)).thenReturn(product);

        assertThrows(OrderException.class, () -> orderItemService.updateItemQuantity(orderItemId, 5));
    }
}
