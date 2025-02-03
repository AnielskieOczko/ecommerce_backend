package com.rj.ecommerce_backend.product.dtos;

import com.rj.ecommerce_backend.product.domain.Product;
import com.rj.ecommerce_backend.product.mappers.ProductSpecifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import java.math.BigDecimal;

@Slf4j
public record ProductSearchCriteria(
        String search,
        String categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer minStockQuantity,
        Integer maxStockQuantity
) {
    public Specification<Product> toSpecification() {
        log.debug("Building specification with criteria: search={}, categoryId={}, price=({} - {}), stock=({} - {})",
                search, categoryId, minPrice, maxPrice, minStockQuantity, maxStockQuantity);
        return Specification
                .where(ProductSpecifications.withSearchCriteria(search))
                .and(ProductSpecifications.withCategory(categoryId))
                .and(ProductSpecifications.withPriceRange(minPrice, maxPrice))
                .and(ProductSpecifications.withStockQuantityRange(minStockQuantity, maxStockQuantity));
    }
}