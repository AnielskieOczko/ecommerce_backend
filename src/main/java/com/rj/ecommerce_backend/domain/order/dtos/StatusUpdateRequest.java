package com.rj.ecommerce_backend.domain.order.dtos;

import com.rj.ecommerce_backend.domain.order.OrderStatus;

public record StatusUpdateRequest(
        OrderStatus newStatus // Use enum instead of String
) {}
