package com.rj.ecommerce_backend.domain.order;

import com.rj.ecommerce_backend.domain.user.User;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
public class OrderSpecifications {

    public static Specification<Order> withSearchCriteria(String search) {
        return (root, query, cb) -> {
            if (StringUtils.isBlank(search)) {
                return null;
            }

            String searchLower = "%" + search.toLowerCase() + "%";
            log.debug("Applying order search criteria: {}", search);

            // Try to parse as order ID
            try {
                Long searchId = Long.parseLong(search);
                return cb.equal(root.get("id"), searchId);
            } catch (NumberFormatException ignored) {
                // Not a numeric ID - continue with other fields
            }

            // Search in user-related fields and transaction ID
            Join<Order, User> userJoin = root.join("user", JoinType.INNER);
            return cb.or(
                    cb.like(cb.lower(userJoin.get("email")), searchLower),
                    cb.like(cb.lower(userJoin.get("firstName")), searchLower),
                    cb.like(cb.lower(userJoin.get("lastName")), searchLower),
                    cb.like(cb.lower(root.get("paymentTransactionId")), searchLower)
            );
        };
    }

    public static Specification<Order> withStatus(OrderStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return null;
            }
            log.debug("Filtering by order status: {}", status);
            return cb.equal(root.get("orderStatus"), status);
        };
    }

    public static Specification<Order> withTotalPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) {
                return null;
            }

            log.debug("Filtering by total price range: {} - {}", minPrice, maxPrice);
            Path<BigDecimal> pricePath = root.get("totalPrice");

            if (minPrice != null && maxPrice != null) {
                return cb.between(pricePath, minPrice, maxPrice);
            }

            if (minPrice != null) {
                return cb.greaterThanOrEqualTo(pricePath, minPrice);
            }

            return cb.lessThanOrEqualTo(pricePath, maxPrice);
        };
    }

    public static Specification<Order> createdBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null) {
                return null;
            }

            log.debug("Filtering orders created between {} and {}", start, end);
            Path<LocalDateTime> createdAtPath = root.get("orderDate");

            if (start != null && end != null) {
                return cb.between(createdAtPath, start, end);
            }

            if (start != null) {
                return cb.greaterThanOrEqualTo(createdAtPath, start);
            }

            return cb.lessThanOrEqualTo(createdAtPath, end);
        };
    }

    public static Specification<Order> withUserId(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) {
                return null;
            }
            log.debug("Filtering orders by user ID: {}", userId);
            return cb.equal(root.get("user").get("id"), userId);
        };
    }

    public static Specification<Order> withPaymentMethod(PaymentMethod paymentMethod) {
        return (root, query, cb) -> {
            if (paymentMethod == null) {
                return null;
            }
            log.debug("Filtering by payment method: {}", paymentMethod);
            return cb.equal(root.get("paymentMethod"), paymentMethod);
        };
    }

    public static Specification<Order> hasTransactionId(Boolean hasTransactionId) {
        return (root, query, cb) -> {
            if (hasTransactionId == null) {
                return null;
            }
            log.debug("Filtering orders with transaction ID: {}", hasTransactionId);
            return hasTransactionId ?
                    cb.isNotNull(root.get("paymentTransactionId")) :
                    cb.isNull(root.get("paymentTransactionId"));
        };
    }
}
