package com.rj.ecommerce_backend.domain.sortingfiltering;

import com.rj.ecommerce_backend.domain.user.exceptions.InvalidSortParameterException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SortValidator {

    private static final String SORT_SEPARATOR = ":";
    private static final Set<String> VALID_DIRECTIONS = Set.of("asc", "desc");


    public Sort validateAndBuildSort(String sortParam, Class<? extends SortableField> sortFieldClass) {
        try {
            return Sort.by(createOrder(sortParam, sortFieldClass));
        } catch (Exception e) {
            throw new InvalidSortParameterException("Invalid sort parameter: " + sortParam);
        }
    }

    private Sort.Order createOrder(String sortPart, Class<? extends SortableField> sortFieldClass) {
        String[] parts = sortPart.split(SORT_SEPARATOR);
        if (parts.length != 2) {
            throw new InvalidSortParameterException("Invalid sort format. Expected 'field:direction'");
        }

        String field = parts[0];
        String direction = parts[1].toLowerCase();

        // Validate field name
        SortableField.fromString(field, sortFieldClass);

        // Validate direction
        if (!VALID_DIRECTIONS.contains(direction)) {
            throw new InvalidSortParameterException("Sort direction must be 'asc' or 'desc'");
        }

        return new Sort.Order(Sort.Direction.fromString(direction), field);
    }
}
