package com.rj.ecommerce_backend.cart.service;

import com.rj.ecommerce_backend.cart.mapper.CartMapper;
import com.rj.ecommerce_backend.cart.domain.Cart;
import com.rj.ecommerce_backend.cart.domain.CartItem;
import com.rj.ecommerce_backend.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.cart.exceptions.ResourceNotFoundException;
import com.rj.ecommerce_backend.cart.repository.CartRepository;
import com.rj.ecommerce_backend.product.domain.Product;
import com.rj.ecommerce_backend.product.repository.ProductRepository;
import com.rj.ecommerce_backend.user.domain.User;
import com.rj.ecommerce_backend.user.repositories.UserRepository;
import com.rj.ecommerce_backend.securityconfig.SecurityContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final  String USER_NOT_FOUND = "User not found with id: ";
    private static final  String CART_NOT_FOUND = "Cart not found for user: ";


    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final SecurityContext securityContext;

    @Override
    @Transactional
    public CartDTO getCartForUser(Long userId) {
        securityContext.checkAccess(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + userId));

        Cart cart = user.getCart();

        if (cart == null) {
            cart = createEmptyCartForUser(user);
        }

        return CartMapper.toDto(cart);
    }


    @Override
    @Transactional
    public Cart createEmptyCartForUser(User user) {
        Cart cart = Cart.builder().build();
        user.setCart(cart);
        cart.setUser(user);
        cartRepository.save(cart);
        return cart;
    }

    @Override
    @Transactional
    public CartDTO addItemToCart(Long userId, Long productId, int quantity) {
        securityContext.checkAccess(userId);
        // Validate inputs
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        // Find or create user's cart
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + userId));

        Cart cart = user.getCart();

        if (cart == null) {
            cart = createEmptyCartForUser(user);
        }

        // Find product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        // Check if product already in cart
        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            // Update quantity if product exists
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // Create new cart item
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cart.getCartItems().add(newCartItem);
        }

        // Save and return updated cart
        Cart savedCart = cartRepository.save(cart);
        return CartMapper.toDto(savedCart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItemQuantity(Long userId, Long cartItemId, int quantity) {
        securityContext.checkAccess(userId);
        // Validate inputs
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        // Find user's cart
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException(CART_NOT_FOUND));

        // Find and update cart item
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        if (quantity == 0) {
            // Remove item if quantity is zero
            cart.getCartItems().remove(cartItem);
        } else {
            cartItem.setQuantity(quantity);
        }

        // Save and return updated cart
        Cart savedCart = cartRepository.save(cart);
        return CartMapper.toDto(savedCart);
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long userId, Long cartItemId) {
        securityContext.checkAccess(userId);
        // Find user's cart
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException(CART_NOT_FOUND + userId));

        // Find and remove cart item
        CartItem cartItemToRemove = cart.getCartItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));

        cart.getCartItems().remove(cartItemToRemove);

        // Save updated cart
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        securityContext.checkAccess(userId);
        // Find user's cart
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException(CART_NOT_FOUND + userId));

        // Clear cart items
        cart.getCartItems().clear();

        // Save updated cart
        cartRepository.save(cart);
    }

}


