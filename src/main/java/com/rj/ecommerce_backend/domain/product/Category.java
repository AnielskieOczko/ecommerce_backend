package com.rj.ecommerce_backend.domain.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Category {

    @Id
    Long id;

    String name;
}
