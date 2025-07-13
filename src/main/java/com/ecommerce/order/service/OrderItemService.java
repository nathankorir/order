package com.ecommerce.order.service;

import com.ecommerce.order.dto.ProductResponseDto;
import com.ecommerce.order.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final RestTemplate restTemplate;
    @Value("${product.service.url:http://localhost:8080/products}")
    private String productServiceUrl;

    public OrderItemService(OrderItemRepository orderItemRepository, RestTemplate restTemplate) {
        this.orderItemRepository = orderItemRepository;
        this.restTemplate = restTemplate;
    }

    private ProductResponseDto getProduct(Long productId) {
        String url = productServiceUrl + "/" + productId;
        ResponseEntity<ProductResponseDto> response = restTemplate.getForEntity(url, ProductResponseDto.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new NoSuchElementException("Product not found with ID: " + productId);
        }
        return response.getBody();
    }
}
