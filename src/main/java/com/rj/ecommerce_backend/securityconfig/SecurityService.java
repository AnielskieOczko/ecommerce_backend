package com.rj.ecommerce_backend.securityconfig;

import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;

    public boolean hasAccessToResource(Long resourceId) {
        // Get current authenticated user
        String currentUsername = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        // Get user from database
        User user = userRepository.findUserByEmail(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if user has access to the resource
        // For regular users, they can only access their own data
        // For admins, they can access all data
        return user.getId().equals(resourceId) ||
                user.getAuthorities().stream().anyMatch(authority -> authority.getName().equals("ROLE_ADMIN"));
    }
}
