package com.rj.ecommerce_backend.product.mapper;

import com.rj.ecommerce_backend.product.domain.Category;
import com.rj.ecommerce_backend.product.domain.Image;
import com.rj.ecommerce_backend.product.domain.Product;
import com.rj.ecommerce_backend.product.dtos.CategoryResponseDTO;
import com.rj.ecommerce_backend.product.dtos.ImageDTO;
import com.rj.ecommerce_backend.product.dtos.ProductCreateDTO;
import com.rj.ecommerce_backend.product.dtos.ProductResponseDTO;
import com.rj.ecommerce_backend.domain.product.valueobject.*;
import com.rj.ecommerce_backend.product.repository.CategoryRepository;
import com.rj.ecommerce_backend.product.valueobject.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductMapper {

    private final CategoryRepository categoryRepository;

    // Constructor dependency injection
    public ProductMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Maps a ProductCreateDTO to a Product entity.
     *
     * This method handles the complex conversion from DTO to entity,
     * including:
     * - Fetching categories by their IDs
     * - Converting image DTOs to Image entities
     * - Mapping value objects for product attributes
     *
     * @param dto The DTO containing product creation information
     * @return A fully mapped Product entity
     */
    public Product mapToEntity(ProductCreateDTO dto) {
        // Fetch categories if category IDs are provided
        List<Category> categories = (dto.categoryIds() != null && !dto.categoryIds().isEmpty())
                ? categoryRepository.findAllById(dto.categoryIds())
                : new ArrayList<>();

        // Convert image DTOs to Image entities
        List<Image> imageList = (dto.imageList() != null && !dto.imageList().isEmpty())
                ? dto.imageList().stream().map(this::mapToImageEntity).toList()
                : new ArrayList<>();

        // Use builder pattern to construct Product with value objects
        return Product.builder()
                .productName(new ProductName(dto.name()))
                .productDescription(new ProductDescription(dto.description()))
                .productPrice(new ProductPrice(
                        new Amount(dto.price()),
                        new CurrencyCode(dto.currencyCode())
                ))
                .stockQuantity(new StockQuantity(dto.quantity()))
                .categories(categories)
                .imageList(imageList)
                .build();
    }

    /**
     * Converts an ImageDTO to an Image entity.
     *
     * @param imageDTO The DTO containing image information
     * @return A mapped Image entity
     */
    public Image mapToImageEntity(ImageDTO imageDTO) {
        return Image.builder()
                .path(imageDTO.path())
                .altText(imageDTO.altText() != null ? imageDTO.altText() : "Product image")
                .mimeType(imageDTO.mimeType())
//                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Maps a Product entity to a ProductResponseDTO.
     *
     * This method handles converting complex entity structure
     * to a flat DTO, including:
     * - Extracting values from value objects
     * - Mapping associated entities (categories, images)
     *
     * @param entity The Product entity to convert
     * @return A fully mapped ProductResponseDTO
     */
    public ProductResponseDTO mapToDTO(Product entity) {
        return new ProductResponseDTO(
                entity.getId(),
                entity.getProductName().value(),
                entity.getProductDescription().value(),
                entity.getProductPrice().amount().value(),
                entity.getStockQuantity().value(),
                // Map categories to CategoryResponseDTOs
                entity.getCategories().stream()
                        .map(this::mapToCategoryDTO)
                        .toList(),
                // Map images to ImageDTOs
                entity.getImageList().stream()
                        .map(this::mapToImageDTO)
                        .toList()
        );
    }

    /**
     * Converts an Image entity to an ImageDTO.
     *
     * @param image The Image entity to convert
     * @return A mapped ImageDTO
     */
    public ImageDTO mapToImageDTO(Image image) {
        return new ImageDTO(
                image.getId(),
                image.getPath(),
                image.getAltText(),
                image.getMimeType()
        );
    }

    /**
     * Maps a Category entity to a CategoryResponseDTO.
     *
     * @param category The Category entity to convert
     * @return A mapped CategoryResponseDTO
     */
    public CategoryResponseDTO mapToCategoryDTO(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName()
        );
    }
}
