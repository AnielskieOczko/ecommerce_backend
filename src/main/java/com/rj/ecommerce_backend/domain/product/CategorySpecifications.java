package com.rj.ecommerce_backend.domain.product;

import com.rj.ecommerce_backend.domain.user.Authority;
import com.rj.ecommerce_backend.domain.user.User;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.Join;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
@AllArgsConstructor
public class CategorySpecifications {

    public static Specification<Category> withSearchCriteria(String search) {
        return (root, query, cb) -> {
            if (StringUtils.isBlank(search)) {
                return null;
            }

            Long searchId = null;
            String searchLower = "%" + search.toLowerCase() + "%";
            log.debug("Applying search criteria: {}", search);

            try {
                searchId = Long.parseLong(search);
            } catch (NumberFormatException e) {
                return cb.like(cb.lower(root.get("name")), searchLower);
            }

            return cb.or(
                    cb.equal(root.get("id"), searchId),
                    cb.like(cb.lower(root.get("name")), searchLower)
            );
        };
    }

    public static Specification<Category> withName(String name) {
        return (root, query, cb) -> {
            if (name == null) {
                return null;
            }
            log.debug("Filtering by category name: {}", name);
            return cb.equal(root.get("name"), name);
        };
    }

}
