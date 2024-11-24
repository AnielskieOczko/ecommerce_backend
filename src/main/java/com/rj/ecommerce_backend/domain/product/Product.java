package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.product.valueobject.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
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

    @ManyToMany
    @JoinTable(
            name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Image> imageList = new ArrayList<>();
}
