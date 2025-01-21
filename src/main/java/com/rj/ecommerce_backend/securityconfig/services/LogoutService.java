package com.rj.ecommerce_backend.securityconfig.services;

import com.rj.ecommerce_backend.securityconfig.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutService implements LogoutHandler {

    private final JwtUtils jwtUtils;
    private final JwtBlacklistService jwtBlacklistService;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        String token = jwtUtils.parseJwt(request);
        String username = token != null ? jwtUtils.getUsernameFromJwtToken(token) : "anonymous";

        if (token != null) {
            jwtBlacklistService.blacklistToken(token, username);
            log.info("Token successfully blacklisted for user: {}", username);
        }

        SecurityContextHolder.clearContext();

    }
}
