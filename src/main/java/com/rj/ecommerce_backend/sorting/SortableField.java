package com.rj.ecommerce_backend.sorting;

import com.rj.ecommerce_backend.user.exceptions.InvalidSortParameterException;

import java.util.Arrays;

public interface SortableField {
    String getFieldName();
    static SortableField fromString(String field, Class<? extends SortableField> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(sortableField -> sortableField.getFieldName().equals(field))
                .findFirst()
                .orElseThrow(() -> new InvalidSortParameterException("Invalid sort field: " + field));
    }
}
