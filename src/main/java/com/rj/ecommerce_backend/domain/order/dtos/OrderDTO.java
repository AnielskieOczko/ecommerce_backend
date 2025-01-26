package com.rj.ecommerce_backend.domain.order.dtos;

import com.rj.ecommerce_backend.domain.order.OrderStatus;
import com.rj.ecommerce_backend.domain.order.PaymentMethod;
import com.rj.ecommerce_backend.domain.order.ShippingMethod;
import com.rj.ecommerce_backend.domain.user.valueobject.Address;
import jakarta.validation.Valid;
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

        @NotEmpty(message = "Order items cannot be empty")
        List<OrderItemDTO> orderItems,

        @Positive(message = "Total price must be positive")
        BigDecimal totalPrice,

        ShippingAddressDTO shippingAddress,

        ShippingMethod shippingMethod,

        @NotNull(message = "Payment method cannot be null")
        PaymentMethod paymentMethod,

        String paymentTransactionId, // Nullable

        LocalDateTime orderDate,

        @NotNull(message = "Order status cannot be null")
        OrderStatus orderStatus, // Use enum instead of String

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
