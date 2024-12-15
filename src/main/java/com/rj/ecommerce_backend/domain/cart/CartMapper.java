package com.rj.ecommerce_backend.domain.cart;

import com.rj.ecommerce_backend.domain.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.domain.cart.dtos.CartItemDTO;
import com.rj.ecommerce_backend.domain.product.Product;

import java.math.BigDecimal;
import java.util.List;

public class CartMapper {

    public static CartDTO toDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        List<CartItemDTO> cartItemDTOs = cart.getCartItems().stream()
                .map(CartMapper::toDto)
                .toList();

        return new CartDTO(cart.getId(), cart.getUser().getId(), cartItemDTOs,
                cart.getCreatedAt(), cart.getUpdatedAt());
    }

    public static CartItemDTO toDto(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        Product product = cartItem.getProduct();
        String productName = (product != null && product.getProductName() != null) ?
                product.getProductName().value() : null;  // Accessing nested record value
        BigDecimal price = (product != null && product.getProductPrice() != null) ?
                product.getProductPrice().amount().value() : null;  // Accessing nested record value


        return new CartItemDTO(cartItem.getId(), cartItem.getCart().getId(),
                (product != null) ? product.getId() : null, productName, cartItem.getQuantity(), price);
    }

    public static Cart toEntity(CartDTO cartDTO) {
        if (cartDTO == null) {
            return null;
        }

        // .user(userService.findById(cartDTO.userId())) // Fetch user from database in your service

        // Don't set cartItems here directly. Handle them in the service layer to manage
        // the bidirectional relationship properly

        return Cart.builder()
                .id(cartDTO.id())
                // .user(userService.findById(cartDTO.userId())) // Fetch user from database in your service
                .createdAt(cartDTO.createdAt())
                .updatedAt(cartDTO.updatedAt())
                .build();
    }

    public static CartItem toEntity(CartItemDTO cartItemDTO) {
        if (cartItemDTO == null) {
            return null;
        }

        // .cart(cartRepository.findById(cartItemDTO.cartId())) // Fetch cart from the database in your service
        // .product(productRepository.findById(cartItemDTO.productId())) // Fetch product from the database
        return CartItem.builder()
                .id(cartItemDTO.id())
                // .cart(cartRepository.findById(cartItemDTO.cartId())) // Fetch cart from the database in your service
                // .product(productRepository.findById(cartItemDTO.productId())) // Fetch product from the database
                .quantity(cartItemDTO.quantity())
                .build();
    }
}
