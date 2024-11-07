package com.rj.ecommerce_backend.domain.user.valueobject;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
public record ZipCode(@NotEmpty String value) {
    // validation
}
