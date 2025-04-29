package com.rj.ecommerce_backend.order.enums;

public enum ShippingMethod {
    INPOST,
    DHL;


    public static ShippingMethod fromString(String shippingMethod) {
        try {
            return ShippingMethod.valueOf(shippingMethod);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid shipping method: " + shippingMethod, e);
        }
    }
}
