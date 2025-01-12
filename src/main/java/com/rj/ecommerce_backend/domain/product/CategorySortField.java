package com.rj.ecommerce_backend.domain.product;

import java.util.Arrays;

public enum CategorySortField {
    ID("id"),
    NAME("name");

    private final String fieldName;

    CategorySortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static CategorySortField fromString(String field) {
        return Arrays.stream(values())
                .filter(sortField -> sortField.fieldName.equals(field))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid sort field: " + field));
    }
}
