package com.rj.ecommerce_backend.securityconfig;

import com.rj.ecommerce_backend.user.domain.User;

public interface SecurityContext {
    void checkAccess(Long userId);
    User getCurrentUser();
    boolean isAdmin();
}
