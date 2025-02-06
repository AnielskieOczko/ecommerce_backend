package com.rj.ecommerce_backend.product.dtos;

import java.math.BigDecimal;
import java.util.List;

public record ProductUpdateDTO(
        String name,
        String description,
        BigDecimal price,
        String currencyCode,
        Integer quantity,
        List<Long> categoryIds,
        List<ImageDTO> imageList) {
}
