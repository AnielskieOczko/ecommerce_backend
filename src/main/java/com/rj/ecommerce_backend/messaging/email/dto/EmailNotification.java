package com.rj.ecommerce_backend.messaging.email.dto;

import com.rj.ecommerce_backend.messaging.email.enums.EmailStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record EmailNotification(
        Long orderId,
        EmailStatus status,
        LocalDateTime timestamp
) {
}
