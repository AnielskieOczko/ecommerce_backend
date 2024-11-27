package com.rj.ecommerce_backend.domain.cart;

import com.rj.ecommerce_backend.BaseMapper;
import com.rj.ecommerce_backend.domain.cart.dtos.CartDTO;
import com.rj.ecommerce_backend.domain.cart.dtos.CartItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = {CartItemMapper.class},  // If you have a separate CartItemMapper
        imports = {ArrayList.class})     // To handle default list initialization
public interface CartMapper extends BaseMapper<CartDTO, Cart> {

    CartItemMapper getCartItemMapper();

    @Mapping(source = "user.id", target = "userId")
    CartDTO toDto(Cart cart);

    @Mapping(target = "user", expression = "java(User.builder().id(cartDTO.userId()).build())")
    Cart toEntity(CartDTO cartDTO);

    default List<CartItem> mapCartItems(List<CartItemDTO> cartItemDTOs) {
        return cartItemDTOs == null ? new ArrayList<>() : cartItemDTOs.stream()
                .map(this::toCartItem)
                .collect(Collectors.toList());
    }

    // Delegate to CartItemMapper
    default CartItem toCartItem(CartItemDTO cartItemDTO) {
        return getCartItemMapper().toEntity(cartItemDTO);
    }


}
