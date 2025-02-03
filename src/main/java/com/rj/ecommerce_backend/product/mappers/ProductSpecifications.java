package com.rj.ecommerce_backend.product.mappers;

import com.rj.ecommerce_backend.product.domain.Category;
import com.rj.ecommerce_backend.product.domain.Product;
import com.rj.ecommerce_backend.product.valueobject.Amount;
import com.rj.ecommerce_backend.product.valueobject.ProductPrice;
import com.rj.ecommerce_backend.user.domain.User;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class ProductSpecifications {

    public static Specification<Product> withSearchCriteria(String search) {
        return (root, query, cb) -> {
            if (StringUtils.isBlank(search)) {
                return null;
            }

            Long searchId = null;
            String searchLower = "%" + search.toLowerCase() + "%";
            log.debug("Applying product search criteria: {}", search);

            try {
                searchId = Long.parseLong(search);
            } catch (NumberFormatException e) {
                return cb.like(cb.lower(root.get("productName")), searchLower);
            }

            return cb.or(
                    cb.equal(root.get("id"), searchId),
                    cb.like(cb.lower(root.get("productName")), searchLower)
            );
        };
    }

    public static Specification<Product> withCategory(String categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return null;
            }
            log.debug("Filtering by product category: {}", categoryId);
            Join<User, Category> categories = root.join("categories");
            return cb.equal(categories.get("id"), categoryId);
        };
    }

    public static Specification<Product> withPriceRange(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) {
                return null;
            }

            BigDecimal minPrice = min != null ? min.setScale(2, RoundingMode.FLOOR) : null;
            BigDecimal maxPrice = max != null ? max.setScale(2, RoundingMode.CEILING) : null;

            log.debug("Filtering by product price: min={}, max={}", minPrice, maxPrice);
//            Path<BigDecimal> pricePath = root.get("productPrice")
//                    .get("amount")
//                    .get("value");

            Join<Product, ProductPrice> priceJoin = root.join("productPrice", JoinType.INNER);
            Join<ProductPrice, Amount> amountJoin = priceJoin.join("amount", JoinType.INNER);
            Path<BigDecimal> valuePath = amountJoin.get("value");

            if (minPrice != null && maxPrice != null) {
                return cb.between(valuePath, minPrice, maxPrice);
            }

            if (minPrice != null) {
                return cb.greaterThanOrEqualTo(valuePath, minPrice);
            }

            return cb.lessThanOrEqualTo(valuePath, maxPrice);

        };
    }

    public static Specification<Product> withStockQuantityRange(Integer min, Integer max) {
        return (root, query, cb) -> {
            if (min == null && max == null) {
                return null;
            }

            log.debug("Filtering by stock quantity: min={}, max={}", min, max);

            Path<Integer> stockQuantity = root.get("stockQuantity")
                    .get("value");

            if (min != null && max != null) {
                return cb.between(stockQuantity, min, max);
            }

            if (min != null) {
                return cb.greaterThanOrEqualTo(stockQuantity, min);
            }

            return cb.lessThanOrEqualTo(stockQuantity, max);
        };
    }
}
