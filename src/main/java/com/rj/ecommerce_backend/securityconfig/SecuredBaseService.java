package com.rj.ecommerce_backend.securityconfig;

import com.rj.ecommerce_backend.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;

@RequiredArgsConstructor
public abstract class SecuredBaseService {

    private final SecurityContextImpl securityContext;

    protected void checkAccess(Long userId) {
        User currentUser = securityContext.getCurrentUser();
        if (!currentUser.getId().equals(userId) && !securityContext.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to access this resource");
        }
    }

    protected User getCurrentUser() {
        return securityContext.getCurrentUser();
    }
}
