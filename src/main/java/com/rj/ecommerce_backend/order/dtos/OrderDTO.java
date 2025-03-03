package com.rj.ecommerce_backend.order.dtos;

import com.rj.ecommerce_backend.order.enums.OrderStatus;
import com.rj.ecommerce_backend.order.enums.PaymentMethod;
import com.rj.ecommerce_backend.order.enums.PaymentStatus;
import com.rj.ecommerce_backend.order.enums.ShippingMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
        Long id,

        @NotNull(message = "User ID cannot be null")
        Long userId,

        @NotNull(message = "Payment method cannot be null")
        String email,

        @NotEmpty(message = "Order items cannot be empty")
        List<OrderItemDTO> orderItems,

        @Positive(message = "Total price must be positive")
        BigDecimal totalPrice,

        ShippingAddressDTO shippingAddress,

        ShippingMethod shippingMethod,

        @NotNull(message = "Payment method cannot be null")
        PaymentMethod paymentMethod,

        String paymentIntentId,
        PaymentStatus paymentStatus,

        String paymentTransactionId, // Nullable

        LocalDateTime orderDate,

        @NotNull(message = "Order status cannot be null")
        OrderStatus orderStatus,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
