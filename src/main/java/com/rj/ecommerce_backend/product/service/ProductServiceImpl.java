package com.rj.ecommerce_backend.product.service;

import com.rj.ecommerce_backend.domain.product.dtos.*;
import com.rj.ecommerce_backend.product.mapper.ProductMapper;
import com.rj.ecommerce_backend.product.domain.Category;
import com.rj.ecommerce_backend.product.domain.Image;
import com.rj.ecommerce_backend.product.domain.Product;
import com.rj.ecommerce_backend.product.dtos.ProductCreateDTO;
import com.rj.ecommerce_backend.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.product.dtos.ProductSearchCriteria;
import com.rj.ecommerce_backend.product.dtos.ProductUpdateDTO;
import com.rj.ecommerce_backend.product.exceptions.FileStorageException;
import com.rj.ecommerce_backend.product.exceptions.ImageNotFoundException;
import com.rj.ecommerce_backend.product.exceptions.InsufficientStockException;
import com.rj.ecommerce_backend.product.exceptions.ProductNotFoundException;
import com.rj.ecommerce_backend.domain.product.valueobject.*;
import com.rj.ecommerce_backend.product.repository.CategoryRepository;
import com.rj.ecommerce_backend.product.repository.ImageRepository;
import com.rj.ecommerce_backend.product.repository.ProductRepository;
import com.rj.ecommerce_backend.product.valueobject.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final FileStorageService fileStorageService;
    private final ProductMapper productMapper;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ProductResponseDTO createProduct(ProductCreateDTO productDTO, List<MultipartFile> images) {

        Product product = productMapper.mapToEntity(productDTO);
        Product savedProduct = productRepository.save(product);

        if (images != null && !images.isEmpty()) {
            List<Image> savedImages = images.stream()
                    .map(file -> fileStorageService.storeFile(file, "Product Image", savedProduct))
                    .toList();
            product.setImageList(savedImages);
        }

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
    public Page<ProductResponseDTO> getAllProducts(Pageable pageable, ProductSearchCriteria criteria) {

        Specification<Product> spec = criteria.toSpecification();

        Page<Product> products = productRepository.findAll(spec, pageable);

        return products.map(productMapper::mapToDTO);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductUpdateDTO productDTO, List<MultipartFile> newImages) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Update basic properties
            ProductPrice updatedProductPrice = new ProductPrice(
                new Amount(productDTO.price()),
                new CurrencyCode(productDTO.currencyCode())
        );

        product.setProductName(new ProductName(productDTO.name()));
        product.setProductDescription(new ProductDescription(productDTO.description()));
        product.setProductPrice(updatedProductPrice);
        product.setStockQuantity(new StockQuantity(productDTO.quantity()));

        // Handle Categories
        if (productDTO.categoryIds() != null && !productDTO.categoryIds().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(productDTO.categoryIds());
            product.setCategories(categories);
        }

        // Handle new images
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile file : newImages) {
                try {
                    Image savedImage = fileStorageService.storeFile(file, "Product Image", product);
                    if (savedImage != null) {
                        savedImage.setProduct(product);
                        product.getImageList().add(savedImage);
                        imageRepository.save(savedImage);
                    }
                } catch (FileStorageException e) {
                    log.error("Failed to store image for product {}: {}", id, e.getMessage());
                }
            }
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.mapToDTO(savedProduct);
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

    @Override
    public void deleteProductImage(Long productId, Long imageId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Image imageToDelete = product.getImageList().stream()
                .filter(image -> image.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ImageNotFoundException(imageId));

        // Remove image from product's list
        product.getImageList().remove(imageToDelete);

        // Delete image file and entity
        fileStorageService.deleteImage(imageToDelete);

        // Save the updated product
        productRepository.save(product);
    }
}



