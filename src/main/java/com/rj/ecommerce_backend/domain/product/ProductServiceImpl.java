package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.dtos.*;
import com.rj.ecommerce_backend.domain.product.exceptions.InsufficientStockException;
import com.rj.ecommerce_backend.domain.product.exceptions.ProductNotFoundException;
import com.rj.ecommerce_backend.domain.product.valueobject.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDTO createProduct(ProductCreateDTO productDTO) {
        Product product = productMapper.mapToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return productMapper.mapToDTO(savedProduct);
    }

    @Override
    public Optional<ProductResponseDTO> getProductById(Long id) {
        return productRepository.findById(id).map(productMapper::mapToDTO);
    }

    @Override
    public Optional<Product> getProductEntityForValidation(Long productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable); // Use findAll(Pageable)

        return products.map(productMapper::mapToDTO);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductUpdateDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Create a new ProductPrice
        ProductPrice updatedProductPrice = new ProductPrice(
                new Amount(productDTO.price()),
                new CurrencyCode(productDTO.currencyCode())
        );


        product = Product.builder()
                .id(product.getId()) // Important: Preserve the ID
                .productName(new ProductName(productDTO.name()))
                .productDescription(new ProductDescription(productDTO.description()))
                .productPrice(updatedProductPrice)
                .stockQuantity(new StockQuantity(productDTO.quantity()))
                //Categories and Images require special handling due to being relationships:
                .categories(product.getCategories())  // Initialize with existing, then update below
                .imageList(product.getImageList()) // Initialize with existing, then update below
                .build();


        // Handle Categories (if provided in the DTO)
        if (productDTO.categoryIds() != null && !productDTO.categoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(productDTO.categoryIds());
            product.setCategories(categories);
        }




        // TODO: handle images -> check it later if it can be done in different way
        if (productDTO.imageList() != null && !productDTO.imageList().isEmpty()) {
            // Clear existing and save/set new images (as in the previous example).
            product.getImageList().clear(); // Clear existing images
            List<Image> newImages = productDTO.imageList().stream()
                    .map(productMapper::mapToImageEntity).toList();
            imageRepository.saveAll(newImages); // Save new images
            product.getImageList().addAll(newImages);
        }


        return productMapper.mapToDTO(productRepository.save(product));
    }

    @Override
    public void reduceProductQuantity(Long productId, int quantityToReduce) {

        Product product = getProductEntityForValidation(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        int currentStock = product.getStockQuantity().value();
        if (currentStock < quantityToReduce) {
            throw new InsufficientStockException("Cannot reduce stock below zero");
        }

        productRepository.updateProductQuantity(productId, new StockQuantity(currentStock - quantityToReduce));
    }


    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductResponseDTO> findProductsByCategory(Long categoryId, Pageable pageable) {
        Page<Product> products = productRepository.findByCategories_Id(categoryId, pageable);
        return products.map(productMapper::mapToDTO);
    }

    @Override
    public Page<ProductResponseDTO> searchProductsByName(String productName, Pageable pageable) {
        Page<Product> products = productRepository.findByProductNameValueContainingIgnoreCase(productName, pageable);
        return products.map(productMapper::mapToDTO);
    }


}
