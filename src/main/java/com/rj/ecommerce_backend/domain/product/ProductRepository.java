package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.valueobject.StockQuantity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends
        JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product>
{

    @Query("SELECT p FROM Product p WHERE p.productName.value LIKE %:name%")
    List<Product> findProductsByNameLike(@Param("name") String name);
    Page<Product> findByCategories_Id(Long categoryId, Pageable pageable);

    Page<Product> findByProductNameValueContainingIgnoreCase(String productName, Pageable pageable);

    Optional<Product> findById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stockQuantity = :newStockQuantity WHERE p.id = :productId")
    void updateProductQuantity(@Param("productId") Long productId,
                               @Param("newStockQuantity") StockQuantity newStockQuantity); // Pass the entire object
}
