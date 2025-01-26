package com.rj.ecommerce_backend.domain.order.dtos;

import com.rj.ecommerce_backend.domain.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.domain.order.PaymentMethod;
import com.rj.ecommerce_backend.domain.order.ShippingMethod;
import jakarta.validation.constraints.NotNull;

public record OrderCreationRequest(
        @NotNull ShippingAddressDTO shippingAddress,
        @NotNull PaymentMethod paymentMethod,
        @NotNull ShippingMethod shippingMethod,
        @NotNull CartDTO cart
) {
}
