package com.rj.ecommerce_backend.domain.user.controllers;

import com.rj.ecommerce_backend.domain.user.dtos.CreateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UpdateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UserResponseDto;
import com.rj.ecommerce_backend.domain.user.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @GetMapping("/email")
    public ResponseEntity<Map<String, String>> getLoggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Map<String, String> response = new HashMap<>();
            response.put("email", email);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        log.info("Getting user with id {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(id));
    }

    @PostMapping("/add-test")
    @Override
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        log.info("Creating user {}", createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(createUserRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest updateUserRequest,
            HttpServletRequest request,
            HttpServletResponse response) {

        log.info("Updating user with id {}", id);
        UserResponseDto updatedUser = userService.updateUser(id, updateUserRequest, request, response);

        String newToken = response.getHeader("Authorization");
        if (newToken != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header("Authorization", newToken)
                    .body(updatedUser);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }

    @DeleteMapping({"/{id}"})
    @Override
    public void deleteUser(@PathVariable Long id) {
        log.info("Removing user with id {}", id);
        userService.deleteUser(id);
    }
}
