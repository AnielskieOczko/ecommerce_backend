package com.rj.ecommerce_backend.domain.user;

import java.util.Arrays;

public enum UserSortField {
    ID("id"),
    EMAIL("email"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    IS_ACTIVE("isActive"),
    AUTHORITIES("authorities"),
    CREATED_AT("createdAt");

    private final String fieldName;

    UserSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public static UserSortField fromString(String field) {
        return Arrays.stream(values())
                .filter(sortField -> sortField.fieldName.equals(field))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid sort field: " + field));
    }
}
