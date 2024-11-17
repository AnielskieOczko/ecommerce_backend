package com.rj.ecommerce_backend.securityconfig;

import com.rj.ecommerce_backend.domain.user.User;

public interface SecurityContext {
    void checkAccess(Long userId);
    User getCurrentUser();
    boolean isAdmin();
}
