package com.rj.ecommerce_backend.domain.sortingfiltering;

public enum CategorySortField implements SortableField {
    ID("id"),
    NAME("name");

    private final String fieldName;

    CategorySortField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

}
