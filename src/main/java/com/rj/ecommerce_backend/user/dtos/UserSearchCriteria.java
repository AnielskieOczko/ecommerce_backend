package com.rj.ecommerce_backend.user.dtos;

import com.rj.ecommerce_backend.user.domain.User;
import com.rj.ecommerce_backend.user.search.UserSpecifications;
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
