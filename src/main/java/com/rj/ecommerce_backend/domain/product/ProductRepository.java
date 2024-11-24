package com.rj.ecommerce_backend.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.productName.value LIKE %:name%")
    List<Product> findProductsByNameLike(@Param("name") String name);
    Page<Product> findByCategories_Id(Long categoryId, Pageable pageable);

    Page<Product> findByProductNameValueContainingIgnoreCase(String productName, Pageable pageable);
}
