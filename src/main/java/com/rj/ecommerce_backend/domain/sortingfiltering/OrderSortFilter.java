package com.rj.ecommerce_backend.domain.sortingfiltering;

public enum OrderSortFilter implements SortableField {
    ID("id"),
    TOTAL("totalPrice"),
    ORDER_DATE("orderDate"),
    STATUS("orderStatus");



    private final String fieldName;

    OrderSortFilter(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }
}
