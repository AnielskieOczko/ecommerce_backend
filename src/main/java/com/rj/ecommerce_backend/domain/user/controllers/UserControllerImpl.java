package com.rj.ecommerce_backend.domain.user.controllers;

import com.rj.ecommerce_backend.domain.user.dtos.*;
import com.rj.ecommerce_backend.domain.user.services.AdminService;
import com.rj.ecommerce_backend.domain.user.services.UserService;
import com.rj.ecommerce_backend.securityconfig.dto.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl {

    private final UserService userService;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserResponseDto> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok().body(userService.getProfile(userId));
    }

    @PutMapping("/{userId}/email")
    public ResponseEntity<AuthResponse> updateUserEmail(
            @PathVariable Long userId,
            @Valid @RequestBody ChangeEmailRequest changeEmailRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        AuthResponse authResponse = userService
                .changeEmail(userId, changeEmailRequest, request, response);

        return ResponseEntity.ok(authResponse);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> updateUserPassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest
            ) {

        userService.changePassword(userId,changePasswordRequest);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/details")
    public ResponseEntity<UserResponseDto> updateUserBasicDetails(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateBasicDetailsRequest updateBasicDetailsRequest
    ) {
        UserResponseDto userResponseDto = userService.updateBasicDetails(userId, updateBasicDetailsRequest);

        return ResponseEntity.ok().body(userResponseDto);
    }

    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponseDto> updateAccountStatus(
            @PathVariable Long userId,
            @Valid @RequestBody AccountStatusRequest request) {

        log.debug("Received request to update account status for user: {}", userId);
        UserResponseDto updatedUser = userService.updateAccountStatus(userId, request);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping({"/{userId}"})
    public ResponseEntity<Void> deleteUserAccount(@PathVariable Long userId) {
        userService.deleteAccount(userId);
        return ResponseEntity.ok().build();
    }
}
