package com.rj.ecommerce_backend.securityconfig.services;

import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.UserDetailsImpl;
import com.rj.ecommerce_backend.securityconfig.RefreshToken;
import com.rj.ecommerce_backend.securityconfig.dto.AuthResponse;
import com.rj.ecommerce_backend.securityconfig.dto.JwtResponse;
import com.rj.ecommerce_backend.securityconfig.dto.LoginRequest;
import com.rj.ecommerce_backend.securityconfig.dto.TokenRefreshRequest;
import com.rj.ecommerce_backend.securityconfig.exceptions.TokenRefreshException;
import com.rj.ecommerce_backend.securityconfig.exceptions.UserAuthenticationException;
import com.rj.ecommerce_backend.securityconfig.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final LogoutService logoutService;


    @Override
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        log.info("Processing authentication request for user: {}", loginRequest.getEmail());
        try {
            // Authenticate the user
            Authentication authentication = performAuthentication(loginRequest);

            JwtResponse jwtResponse = generateAuthResponse(authentication);

            // Create and return the response
            return AuthResponse.builder()
                    .success(true)
                    .message("Authentication successful")
                    .data(jwtResponse)
                    .build();

        } catch (UserAuthenticationException e) {
            log.error("Authentication failed for user {}: {}", loginRequest.getEmail(), e.getMessage());
            return AuthResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        log.info("Processing token refresh request");

        try {
            RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
            JwtResponse jwtResponse = generateNewTokens(refreshToken.getUser());

            return AuthResponse.builder()
                    .success(true)
                    .message("Token refresh successful")
                    .data(jwtResponse)
                    .build();

        } catch (TokenRefreshException e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return AuthResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    public AuthResponse handleEmailUpdate(
            User user,
            String currentPassword,
            HttpServletRequest request,
            HttpServletResponse response) {

        log.info("Updating authentication for user {} after email change", user.getId());

        try {
            // Logout current user session
            logoutCurrentUser(request, response);

            // Re-authenticate with new email
            Authentication newAuth = authenticateWithNewEmail(user.getEmail().value(), currentPassword);

            // Generate new tokens
            String newJwtToken = jwtUtils.generateJwtToken(newAuth);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

            // Update response header with new token
            response.setHeader(AUTH_HEADER,
                    TOKEN_PREFIX + newJwtToken);

            // Create response with new tokens
            JwtResponse jwtResponse = new JwtResponse(
                    newJwtToken,
                    refreshToken.getToken(),
                    user.getId(),
                    user.getEmail().value(),
                    extractRoles(newAuth)
            );

            log.info("Successfully updated authentication for user {}", user.getId());

            return AuthResponse.builder()
                    .success(true)
                    .message("Authentication updated successfully")
                    .data(jwtResponse)
                    .build();

        } catch (Exception e) {
            log.error("Failed to update authentication after email change for user {}", user.getId(), e);
            return AuthResponse.builder()
                    .success(false)
                    .message("Failed to update authentication: " + e.getMessage())
                    .build();
        }
    }

    private Authentication performAuthentication(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Successfully authenticated user: {}", loginRequest.getEmail());
            return authentication;

        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials provided for user: {}", loginRequest.getEmail());
            throw new UserAuthenticationException("Invalid email or password");
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequest.getEmail(), e);
            throw new UserAuthenticationException("Authentication failed: " + e.getMessage());
        }
    }

    private JwtResponse generateAuthResponse(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new JwtResponse(
                jwt,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                roles
        );
    }

    private JwtResponse generateNewTokens(User user) {
        try {
            UserDetailsImpl userDetails = UserDetailsImpl.build(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            String accessToken = jwtUtils.generateJwtToken(authentication);
            RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

            return new JwtResponse(
                    accessToken,
                    newRefreshToken.getToken(),
                    user.getId(),
                    user.getEmail().value(),
                    userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList()
            );
        } catch (Exception e) {
            log.error("Failed to generate new tokens for user: {}", user.getId(), e);
            throw new TokenRefreshException("Failed to generate new tokens: " + e.getMessage());
        }
    }

    private void logoutCurrentUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth != null) {
            logoutService.logout(request, response, currentAuth);
            log.debug("Logged out current user session");
        }
    }

    private Authentication authenticateWithNewEmail(String email, String password) {
        Authentication newAuth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
        return newAuth;
    }

    private List<String> extractRoles(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
