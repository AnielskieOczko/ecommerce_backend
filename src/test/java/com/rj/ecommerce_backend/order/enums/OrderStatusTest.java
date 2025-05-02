package com.rj.ecommerce_backend.order.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    void shouldConvertValidStringToOrderStatus() {
        // Given & When & Then
        assertEquals(OrderStatus.PENDING, OrderStatus.fromString("PENDING"));
        assertEquals(OrderStatus.CONFIRMED, OrderStatus.fromString("CONFIRMED"));
        assertEquals(OrderStatus.PROCESSING, OrderStatus.fromString("PROCESSING"));
        assertEquals(OrderStatus.SHIPPED, OrderStatus.fromString("SHIPPED"));
        assertEquals(OrderStatus.DELIVERED, OrderStatus.fromString("DELIVERED"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.fromString("CANCELLED"));
        assertEquals(OrderStatus.REFUNDED, OrderStatus.fromString("REFUNDED"));
        assertEquals(OrderStatus.FAILED, OrderStatus.fromString("FAILED"));
    }

    @Test
    void shouldConvertAllEnumValuesToString() {
        // Test all enum values can be converted to string and back
        for (OrderStatus status : OrderStatus.values()) {
            assertEquals(status, OrderStatus.fromString(status.name()));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "pending", "Confirmed", "", "UNKNOWN"})
    void shouldThrowExceptionForInvalidOrderStatus(String invalidStatus) {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> OrderStatus.fromString(invalidStatus)
        );
        
        assertTrue(exception.getMessage().contains("Invalid order status"));
    }
}
