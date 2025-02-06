package com.rj.ecommerce_backend.order.search;

import com.rj.ecommerce_backend.order.domain.Order;
import com.rj.ecommerce_backend.order.enums.OrderStatus;
import com.rj.ecommerce_backend.order.enums.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
public record OrderSearchCriteria(
        String search,
        OrderStatus status,
        BigDecimal minTotal,
        BigDecimal maxTotal,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Long userId,
        PaymentMethod paymentMethod,
        Boolean hasTransactionId
) {
    public Specification<Order> toSpecification() {
        log.debug("Building order specification with criteria: [search={}, status={}, total={}-{}, dates={}-{}, user={}, payment={}, hasTransactionId={}]",
                search, status, minTotal, maxTotal, startDate, endDate, userId, paymentMethod, hasTransactionId);

        return Specification
                .where(OrderSpecifications.withSearchCriteria(search))
                .and(OrderSpecifications.withStatus(status))
                .and(OrderSpecifications.withTotalPriceRange(minTotal, maxTotal))
                .and(OrderSpecifications.createdBetween(startDate, endDate))
                .and(OrderSpecifications.withUserId(userId))
                .and(OrderSpecifications.withPaymentMethod(paymentMethod))
                .and(OrderSpecifications.hasTransactionId(hasTransactionId));
    }
}
