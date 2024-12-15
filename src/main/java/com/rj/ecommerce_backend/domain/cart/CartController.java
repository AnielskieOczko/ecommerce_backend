package com.rj.ecommerce_backend.domain.cart;

import com.rj.ecommerce_backend.domain.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.securityconfig.SecurityContextImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/cart")
@RestController
public class CartController {

    private final CartService cartService;
    private final SecurityContextImpl securityContext;

    @GetMapping
    public ResponseEntity<CartDTO> getUserCart(@RequestParam Long userId) {
        securityContext.checkAccess(userId);
        return ResponseEntity.ok(cartService.getCartForUser(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addItemToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        securityContext.checkAccess(userId);
        return ResponseEntity.ok(cartService.addItemToCart(userId, productId, quantity));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartDTO> updateCartItemQuantity(
            @RequestParam Long userId,
            @PathVariable Long cartItemId,
            @RequestParam int quantity
    ) {
        securityContext.checkAccess(userId);
        return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, cartItemId, quantity));
    }

    @DeleteMapping("items/{cartItemId}")
    public ResponseEntity<Void> removeItemFromCart(
            @RequestParam Long userId,
            @PathVariable Long cartItemId) {
        securityContext.checkAccess(userId);
        cartService.removeItemFromCart(userId, cartItemId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestParam Long userId) {
        securityContext.checkAccess(userId);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }


}
