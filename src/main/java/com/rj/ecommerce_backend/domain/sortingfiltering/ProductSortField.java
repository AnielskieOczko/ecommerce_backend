package com.rj.ecommerce_backend.domain.sortingfiltering;

public enum ProductSortField implements SortableField {
    ID("id"),
    CATEGORIES("categories"),
    QUANTITY("stockQuantity"),
    PRICE("productPrice"),
    NAME("productName");

    private final String fieldName;

    ProductSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }
}
