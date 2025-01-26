package com.rj.ecommerce_backend.domain.order;

import com.rj.ecommerce_backend.domain.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends
        JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
}
