package com.rj.ecommerce_backend.domain.product.dtos;

import com.rj.ecommerce_backend.domain.product.Category;
import com.rj.ecommerce_backend.domain.product.CategorySpecifications;
import org.springframework.data.jpa.domain.Specification;

public record CategorySearchCriteria(
        String search,
        String name) {
    public Specification<Category> toSpecification() {
        return Specification
                .where(CategorySpecifications.withSearchCriteria(search))
                .and(CategorySpecifications.withName(name));
    }
}
