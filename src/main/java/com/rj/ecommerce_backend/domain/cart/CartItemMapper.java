package com.rj.ecommerce_backend.domain.cart;

import com.rj.ecommerce_backend.domain.cart.dtos.CartItemDTO;
import com.rj.ecommerce_backend.domain.product.ProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring",
        uses = {ProductMapper.class},  // If you have product-related mapping
        imports = {ArrayList.class})
public interface CartItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    CartItemDTO toDto(CartItem cartItem);

    @Mapping(target = "product", expression = "java(Product.builder().id(cartItemDTO.productId()).build())")
    @Mapping(target = "cart", ignore = true)
    CartItem toEntity(CartItemDTO cartItemDTO);
}
