package com.rj.ecommerce_backend.order.dtos;

import com.rj.ecommerce_backend.order.enums.OrderStatus;

public record StatusUpdateRequest(
        OrderStatus newStatus // Use enum instead of String
) {}
