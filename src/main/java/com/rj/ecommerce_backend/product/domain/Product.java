package com.rj.ecommerce_backend.product.domain;

import com.rj.ecommerce_backend.product.valueobject.ProductDescription;
import com.rj.ecommerce_backend.product.valueobject.ProductName;
import com.rj.ecommerce_backend.product.valueobject.ProductPrice;
import com.rj.ecommerce_backend.product.valueobject.StockQuantity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "name"))
    ProductName productName;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "description"))
    ProductDescription productDescription;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "price"))
    ProductPrice productPrice;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quantity"))
    StockQuantity stockQuantity;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    List<Category> categories = new ArrayList<>();

    @OneToMany(
            mappedBy = "product",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = true
    )
    List<Image> imageList = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
