package com.rj.ecommerce_backend.securityconfig;

import com.rj.ecommerce_backend.domain.user.dtos.CreateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UserResponseDto;
import com.rj.ecommerce_backend.domain.user.services.UserService;
import com.rj.ecommerce_backend.securityconfig.dto.*;
import com.rj.ecommerce_backend.securityconfig.exception.UserAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final LogoutService logoutService;
    private final JwtBlacklistService jwtBlackListedService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        log.info("Creating user {}", createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(createUserRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(AuthResponse.builder()
                    .success(true)
                    .data(jwtResponse)
                    .build());
        } catch (UserAuthenticationException e) {
            log.error("Authentication failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Unexpected error during authentication", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponse.builder()
                            .success(false)
                            .message("An unexpected error occurred")
                            .build());
        }
    }

    @GetMapping("/user/{userId}/tokens")
    public ResponseEntity<List<TokenInfo>> getUserTokens(@PathVariable Long userId) {
        return ResponseEntity.ok(jwtBlackListedService.getUserTokens(userId));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

}
