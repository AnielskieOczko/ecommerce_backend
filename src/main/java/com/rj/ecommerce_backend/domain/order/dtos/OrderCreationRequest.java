package com.rj.ecommerce_backend.domain.order.dtos;

import com.rj.ecommerce_backend.domain.cart.dtos.CartDTO;

public record OrderCreationRequest(
        AddressDTO shippingAddress,
        String paymentMethod,
        CartDTO cart
) {
}
