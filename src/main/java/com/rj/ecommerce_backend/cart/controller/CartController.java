package com.rj.ecommerce_backend.cart.controller;

import com.rj.ecommerce_backend.cart.service.CartService;
import com.rj.ecommerce_backend.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.cart.dtos.CartItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users/{userId}/cart")
@RestController
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDTO> getUserCart(@RequestParam Long userId) {
        return ResponseEntity.ok(cartService.getCartForUser(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(
            @PathVariable Long userId,
            @RequestBody CartItemRequest cartItemRequest,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        return ResponseEntity
                .ok(cartService.addItemToCart(userId, cartItemRequest.productId(), cartItemRequest.quantity()));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartDTO> updateCartItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long cartItemId,
            @RequestParam int quantity
    ) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, cartItemId, quantity));
    }

    @DeleteMapping("items/{cartItemId}")
    public ResponseEntity<Void> removeItemFromCart(
            @RequestParam Long userId,
            @PathVariable Long cartItemId) {
        cartService.removeItemFromCart(userId, cartItemId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }


}
