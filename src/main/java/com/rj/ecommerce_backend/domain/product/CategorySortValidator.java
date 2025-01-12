package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.user.exceptions.InvalidSortParameterException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class CategorySortValidator {

    public Sort validateAndBuildSort(String sortParam) {
        try {
            return Sort.by(createOrder(sortParam));
        } catch (Exception e) {
            throw new InvalidSortParameterException("Invalid sort parameter: " + sortParam);
        }
    }

    private Sort.Order createOrder(String sortPart) {
        String[] parts = sortPart.split(":");
        if (parts.length != 2) {
            throw new InvalidSortParameterException("Invalid sort format. Expected 'field:direction'");
        }

        String field = parts[0];
        String direction = parts[1].toLowerCase();

        // Validate field name
        CategorySortField.fromString(field);

        // Validate direction
        if (!direction.equals("asc") && !direction.equals("desc")) {
            throw new InvalidSortParameterException("Sort direction must be 'asc' or 'desc'");
        }

        return new Sort.Order(Sort.Direction.fromString(direction), field);
    }
}

