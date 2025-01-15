package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.domain.sortingfiltering.SortValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Public Product API", description = "Public APIs for viewing products")
@RestController
@RequestMapping("/api/v1/public/products")
@Slf4j
public class PublicProductController extends BaseProductController {

    public PublicProductController(ProductService productService,
                                   FileStorageService fileStorageService,
                                   SortValidator sortValidator) {
        super(productService, fileStorageService, sortValidator);
    }

}
