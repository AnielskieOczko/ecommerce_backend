package com.rj.ecommerce_backend.domain.product.dtos;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer quantity,
        List<CategoryResponseDTO> categories,
        List<ImageDTO> imageList
) {
}
