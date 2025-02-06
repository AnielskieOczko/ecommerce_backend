package com.rj.ecommerce_backend.product.dtos;

import com.rj.ecommerce_backend.product.domain.Category;
import com.rj.ecommerce_backend.product.search.CategorySpecifications;
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
