package com.rj.ecommerce_backend.domain.product.dtos;

import org.springframework.util.MimeType;

public record ImageDTO(Long id, String path, String altText, String mimeType) {

}
