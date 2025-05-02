package com.rj.ecommerce_backend.order.dtos;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemDTOTest {

    @Test
    void shouldCreateValidOrderItemDTO() {
        // Given
        Long id = 1L;
        Long orderId = 100L;
        Long productId = 200L;
        String productName = "Test Product";
        int quantity = 2;
        BigDecimal price = new BigDecimal("99.99");

        // When
        OrderItemDTO orderItemDTO = new OrderItemDTO(id, orderId, productId, productName, quantity, price);

        // Then
        assertEquals(id, orderItemDTO.id());
        assertEquals(orderId, orderItemDTO.orderId());
        assertEquals(productId, orderItemDTO.productId());
        assertEquals(productName, orderItemDTO.productName());
        assertEquals(quantity, orderItemDTO.quantity());
        assertEquals(price, orderItemDTO.price());
    }

    @Test
    void shouldAllowNullId() {
        // Given & When
        OrderItemDTO orderItemDTO = new OrderItemDTO(null, 100L, 200L, "Test Product", 2, new BigDecimal("99.99"));

        // Then
        assertNull(orderItemDTO.id());
    }

    @Test
    void shouldAllowNullOrderId() {
        // Given & When
        OrderItemDTO orderItemDTO = new OrderItemDTO(1L, null, 200L, "Test Product", 2, new BigDecimal("99.99"));

        // Then
        assertNull(orderItemDTO.orderId());
    }

    @Test
    void shouldAllowNullProductId() {
        // Given & When
        OrderItemDTO orderItemDTO = new OrderItemDTO(1L, 100L, null, "Test Product", 2, new BigDecimal("99.99"));

        // Then
        assertNull(orderItemDTO.productId());
    }

    @Test
    void shouldAllowNullProductName() {
        // Given & When
        OrderItemDTO orderItemDTO = new OrderItemDTO(1L, 100L, 200L, null, 2, new BigDecimal("99.99"));

        // Then
        assertNull(orderItemDTO.productName());
    }

    @Test
    void shouldAllowZeroQuantity() {
        // Given & When
        OrderItemDTO orderItemDTO = new OrderItemDTO(1L, 100L, 200L, "Test Product", 0, new BigDecimal("99.99"));

        // Then
        assertEquals(0, orderItemDTO.quantity());
    }

    @Test
    void shouldAllowNegativeQuantity() {
        // Given & When
        OrderItemDTO orderItemDTO = new OrderItemDTO(1L, 100L, 200L, "Test Product", -1, new BigDecimal("99.99"));

        // Then
        assertEquals(-1, orderItemDTO.quantity());
    }

    @Test
    void shouldAllowNullPrice() {
        // Given & When
        OrderItemDTO orderItemDTO = new OrderItemDTO(1L, 100L, 200L, "Test Product", 2, null);

        // Then
        assertNull(orderItemDTO.price());
    }
}
