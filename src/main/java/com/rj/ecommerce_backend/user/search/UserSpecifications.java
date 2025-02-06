package com.rj.ecommerce_backend.user.search;

import com.rj.ecommerce_backend.user.domain.Authority;
import com.rj.ecommerce_backend.user.domain.User;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Join;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecifications {

    public static Specification<User> withSearchCriteria(String search) {
        return (root, query, cb) -> {
            if (StringUtils.isBlank(search)) {
                return null;
            }

            String searchLower = "%" + search.toLowerCase() + "%";
            log.debug("Applying search criteria: {}", search);

            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), searchLower),
                    cb.like(cb.lower(root.get("lastName")), searchLower),
                    cb.like(cb.lower(root.get("email").get("value")), searchLower)
            );
        };
    }

    public static Specification<User> withActiveStatus(Boolean isActive) {
        return (root, query, cb) -> {
            if (isActive == null) {
                return null;
            }
            log.debug("Filtering by active status: {}", isActive);
            return cb.equal(root.get("isActive"), isActive);
        };
    }

    public static Specification<User> withRole(String role) {
        return (root, query, cb) -> {
            if (StringUtils.isBlank(role)) {
                return null;
            }
            log.debug("Filtering by role: {}", role);

            Join<User, Authority> authorities = root.join("authorities");
            return cb.equal(authorities.get("name"), role);
        };
    }
}
