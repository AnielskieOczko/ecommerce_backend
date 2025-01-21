package com.rj.ecommerce_backend.securityconfig;

import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.repositories.UserRepository;
import com.rj.ecommerce_backend.domain.user.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityContextImpl implements SecurityContext{

    private final UserRepository userRepository;

    @Override
    public void checkAccess(Long userId) {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId) && isAdmin()) {
            throw new AccessDeniedException("You don't have permission to access this resource");
        }
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findUserByEmail(new Email(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public boolean isAdmin() {
        return getCurrentUser().getAuthorities().stream()
                .noneMatch(authority -> authority.getName().equals("ROLE_ADMIN"));
    }
}
