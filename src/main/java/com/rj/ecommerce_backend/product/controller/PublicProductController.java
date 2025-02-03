package com.rj.ecommerce_backend.product.controller;

import com.rj.ecommerce_backend.product.service.FileStorageService;
import com.rj.ecommerce_backend.product.service.ProductService;
import com.rj.ecommerce_backend.sorting.SortValidator;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
