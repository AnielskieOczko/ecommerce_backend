package com.rj.ecommerce_backend.securityconfig;

import com.rj.ecommerce_backend.domain.user.UserDetailsImpl;
import com.rj.ecommerce_backend.domain.user.UserRepository;
import com.rj.ecommerce_backend.securityconfig.dto.JwtResponse;
import com.rj.ecommerce_backend.securityconfig.dto.LoginRequest;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtUtils jwtUtils;




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

            // Generate JWT token
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Get user details
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            // Create and return the response
            return new JwtResponse(
                    jwt,
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
    public JwtResponse refreshToken(String refreshToken) {
        // Implementation for refresh token logic
        // This is optional but recommended for better security
        try {
            if (!jwtUtils.validateJwtToken(refreshToken)) {
                throw new UserAuthenticationException("Invalid refresh token");
            }

            String userEmail = jwtUtils.getUsernameFromJwtToken(refreshToken);
            UserDetails userDetails = userRepository.findUserByEmail(userEmail)
                    .map(UserDetailsImpl::build)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String newToken = jwtUtils.generateJwtToken(authentication);

            return new JwtResponse(
                    newToken,
                    ((UserDetailsImpl) userDetails).getId(),
                    userDetails.getUsername(),
                    userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList()
            );
        } catch (Exception e) {
            log.error("Failed to refresh token", e);
            throw new UserAuthenticationException("Failed to refresh token");
        }
    }

}
