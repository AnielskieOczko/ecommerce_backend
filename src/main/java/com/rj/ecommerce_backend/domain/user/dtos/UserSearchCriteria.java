package com.rj.ecommerce_backend.domain.user.dtos;

import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.services.UserSpecifications;
import org.springframework.data.jpa.domain.Specification;

public record UserSearchCriteria(
        String search,
        Boolean isActive,
        String authority
) {


    public Specification<User> toSpecification() {
        return Specification
                .where(UserSpecifications.withSearchCriteria(search))
                .and(UserSpecifications.withActiveStatus(isActive))
                .and(UserSpecifications.withRole(authority));
    }
}
