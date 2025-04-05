package com.rj.ecommerce_backend.testdata;

import com.rj.ecommerce_backend.product.domain.Category;
import com.rj.ecommerce_backend.product.domain.Image;
import com.rj.ecommerce_backend.product.domain.Product;
import com.rj.ecommerce_backend.product.exceptions.CategoryAlreadyExistsException;
import com.rj.ecommerce_backend.product.repository.CategoryRepository;
import com.rj.ecommerce_backend.product.repository.ProductRepository;
import com.rj.ecommerce_backend.product.service.FileStorageService;
import com.rj.ecommerce_backend.product.valueobject.*;
import com.rj.ecommerce_backend.user.domain.Authority;
import com.rj.ecommerce_backend.user.dtos.AddressDto;
import com.rj.ecommerce_backend.user.dtos.CreateUserRequest;
import com.rj.ecommerce_backend.user.dtos.PhoneNumberDto;
import com.rj.ecommerce_backend.user.services.AdminServiceImpl;
import com.rj.ecommerce_backend.user.services.AuthorityServiceImpl;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
@AllArgsConstructor
public class TestDataLoader {

    private final AdminServiceImpl adminService;
    private final AuthorityServiceImpl authorityServiceImpl;
    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    private final PathMatchingResourcePatternResolver resourcePatternResolver;

    private static final Random RANDOM = new Random();

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

//    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStartUp() {

//        log.info("Load test authorities");
//        loadAuthorities();

        log.info("Load test users");
        loadUsers();

        log.info("Load test categories");
        createInitialCategories();

        log.info("Load test products");
        createInitialProducts(20);
    }


    @Transactional
    private void loadAuthorities() {
        Authority authority = new Authority();
        authority.setName(ROLE_USER);

        Authority authority1 = new Authority();
        authority1.setName(ROLE_ADMIN);

        authorityServiceImpl.addNewAuthority(authority);
        authorityServiceImpl.addNewAuthority(authority1);
    }

    @Transactional
    private void loadUsers() {

        AddressDto addressDto = new AddressDto(
                "new street",
                "Wroclaw",
                "55-200",
                "Poland"
        );

        PhoneNumberDto phoneNumberDto = new PhoneNumberDto("777-777-777");

        CreateUserRequest createUserRequest = new CreateUserRequest(
                "firstName",
                "lastName",
                "root@gmail.com",
                "root",
                addressDto,
                phoneNumberDto,
                LocalDate.now(),
                Set.of(ROLE_ADMIN)
        );

        adminService.createUser(createUserRequest);

        // Generate 20 additional users
        for (int i = 1; i <= 20; i++) {
            AddressDto userAddress = new AddressDto(
                    "Street " + i,
                    "City " + i,
                    String.format("%05d", i), // Example zip code generation
                    "Country " + i
            );

            PhoneNumberDto userPhone = new PhoneNumberDto(String.format("555-555-%04d", i));

            CreateUserRequest userRequest = new CreateUserRequest(
                    "FirstName" + i,
                    "LastName" + i,
                    "user" + i + "@example.com", // Unique email
                    "password" + i,
                    userAddress,
                    userPhone,
                    LocalDate.now().minusYears(20L + i), // Varied birthdays
                    Set.of(ROLE_USER)
            );

            try {
                adminService.createUser(userRequest);
            } catch (Exception e) {
                // Handle potential errors like unique constraint violations.  Log and continue.
                log.error("Error creating user {}: {}", i, e.getMessage());
            }
        }

    }

    @Transactional // Important for multiple saves
    public void createInitialCategories() {
        List<String> categoryNames = Arrays.asList(
                "T-Shirts",
                "Shirts",
                "Jeans",
                "Dresses",
                "Skirts",
                "Jackets",
                "Sweaters",
                "Hoodies",
                "Pants",
                "Shorts"
        );

        for (String name : categoryNames) {
            try {
                createCategoryIfNotExist(name);
            } catch (CategoryAlreadyExistsException e) {
                // Log and continue to the next category.
                log.warn("Category {} already exists. Skipping.", name);
            } catch (Exception e) {
                log.error("Error creating category {}: {}", name, e.getMessage());
            }
        }
    }

    @Transactional
    public void createInitialProducts(int numProducts) {
        List<Category> allCategories = categoryRepository.findAll();

        if (allCategories.isEmpty()) {
            log.warn("No categories found. Cannot create products.");
            return;
        }

        try {
            Resource[] imageResources = resourcePatternResolver.getResources("classpath:static/product-images/*");
            log.info("Found {} image resources", imageResources.length);

            for (int i = 1; i <= numProducts; i++) {
                createAndSaveProduct(i, allCategories, imageResources);
            }

            log.info("Successfully created {} products", numProducts);
        } catch (IOException e) {
            log.error("Error loading image resources: {}", e.getMessage());
        }
    }

    private void createAndSaveProduct(int i, List<Category> allCategories, Resource[] imageResources) {
        try {
            // Create and save the product
            Product savedProduct = createProduct(i, allCategories);

            // Add image if available
            if (imageResources.length > 0) {
                Resource imageResource = imageResources[(i - 1) % imageResources.length];
                saveImagesForProducts(savedProduct, imageResource, i);
            }
        } catch (Exception e) {
            log.error("Error creating product {}: {}", i, e.getMessage());
        }
    }

    private void saveImagesForProducts(Product savedProduct, Resource imageResource, int i) {
        try {
            addImageToProduct(savedProduct, imageResource);
        } catch (IOException e) {
            log.error("Error processing image for product {}: {}", i, e.getMessage());
        }
    }

    private Product createProduct(int index, List<Category> categories) {
        String productName = "Product " + index;
        Product product = Product.builder()
                .productName(new ProductName(productName))
                .productDescription(new ProductDescription("Description for " + productName))
                .productPrice(new ProductPrice(
                        new Amount(BigDecimal.valueOf(10.00 + index)),
                        new CurrencyCode("USD")))
                .stockQuantity(new StockQuantity(10 + index))
                .categories(Collections.singletonList(getRandomCategory(categories)))
                .build();

        return productRepository.save(product);
    }


    private Category getRandomCategory(List<Category> categories) {
        return categories.get(RANDOM.nextInt(categories.size()));
    }


    private void createCategoryIfNotExist(String name) {
        if (categoryRepository.findByName(name).isEmpty()) {
            Category category = new Category();
            category.setName(name);
            categoryRepository.save(category);
            log.info("Created category: {}", name); // Log successful creation
        }
    }

    private void addImageToProduct(Product product, Resource imageResource) throws IOException {
        Path imagePath = Path.of(imageResource.getURI());
        String fileName = imagePath.getFileName().toString();
        byte[] fileContent = Files.readAllBytes(imagePath);
        String contentType = Files.probeContentType(imagePath);

        MultipartFile multipartFile = new CustomMultipartFile(fileContent, fileName, contentType);
        Image savedImage = fileStorageService.storeFile(multipartFile, "Product Image", product);
        product.getImageList().add(savedImage);
        productRepository.save(product);
    }

    // Custom MultipartFile implementation
    private static class CustomMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String name;
        private final String contentType;

        public CustomMultipartFile(byte[] content, String name, String contentType) {
            this.content = content;
            this.name = name;
            this.contentType = contentType;
        }

        @Override
        public String getName() {
            log.debug("Getting file name");
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return name;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File destination) throws IOException, IllegalStateException {
            Files.write(destination.toPath(), content);
        }
    }
}
