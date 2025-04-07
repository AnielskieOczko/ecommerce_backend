package com.rj.ecommerce_backend.order.dtos;

import com.rj.ecommerce_backend.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.order.enums.PaymentMethod;
import com.rj.ecommerce_backend.order.enums.ShippingMethod;
import jakarta.validation.constraints.NotNull;

public record OrderCreationRequest(
        @NotNull ShippingAddressDTO shippingAddress,
        // payment method will be provided by stripe api (user select it in checkout)
        PaymentMethod paymentMethod,
        @NotNull ShippingMethod shippingMethod,
        @NotNull CartDTO cart
) {
}
