package com.rj.ecommerce_backend.sorting;

public enum UserSortField implements SortableField {
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

    @Override
    public String getFieldName() {
        return fieldName;
    }
}
