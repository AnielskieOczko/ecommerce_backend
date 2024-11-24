package com.rj.ecommerce_backend.securityconfig;

import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.UserDetailsImpl;
import com.rj.ecommerce_backend.securityconfig.dto.JwtResponse;
import com.rj.ecommerce_backend.securityconfig.dto.LoginRequest;
import com.rj.ecommerce_backend.securityconfig.dto.TokenRefreshRequest;
import com.rj.ecommerce_backend.securityconfig.exception.UserAuthenticationException;
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

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword())
            );

            // Set the authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            // Generate JWT token
            String jwt = jwtUtils.generateJwtToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            // Create and return the response
            return new JwtResponse(
                    jwt,
                    refreshToken.getToken(),
                    userDetails.getId(),
                    userDetails.getUsername(),
                    roles
            );
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", loginRequest.getEmail());
            throw new UserAuthenticationException("Invalid email or password");
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", loginRequest.getEmail(), e);
            throw new UserAuthenticationException("Authentication failed");
        }
    }

    @Override
    public JwtResponse refreshToken(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        User user = refreshToken.getUser();

        // Generate new tokens
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

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
    }


}
