package com.ecommerce.order.service;

import com.ecommerce.order.dto.ProductResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ProductServiceUtils {
    private final RestTemplate restTemplate;
    @Value("${product.service.url:http://products-service:8080/products}")
    private String productServiceUrl;

    public ProductServiceUtils(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProductResponseDto getProduct(UUID productId) {
        String url = productServiceUrl + "/" + productId;
        ResponseEntity<ProductResponseDto> response = restTemplate.getForEntity(url, ProductResponseDto.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new NoSuchElementException("Product not found with ID: " + productId);
        }
        return response.getBody();
    }

    public void dispenseInventory(UUID productId, int quantity) {
        String url = productServiceUrl + "/" + productId + "/dispense";
        restTemplate.postForEntity(url, createQuantityPayload(quantity), Void.class);
    }

    public void restockInventory(UUID productId, int quantity) {
        String url = productServiceUrl + "/" + productId + "/restock";
        restTemplate.postForEntity(url, createQuantityPayload(quantity), Void.class);
    }

    public static java.util.Map<String, Integer> createQuantityPayload(int quantity) {
        return java.util.Collections.singletonMap("quantity", quantity);
    }
}
