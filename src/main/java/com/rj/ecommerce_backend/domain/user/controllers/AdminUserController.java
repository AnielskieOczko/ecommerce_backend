package com.rj.ecommerce_backend.domain.user.controllers;

import com.rj.ecommerce_backend.domain.sortingfiltering.SortValidator;
import com.rj.ecommerce_backend.domain.sortingfiltering.UserSortField;
import com.rj.ecommerce_backend.domain.user.dtos.*;
import com.rj.ecommerce_backend.domain.user.services.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final AdminService adminService;
    private final SortValidator sortValidator;


    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        log.info("Received request to retrieve user with id: {}", userId);
        UserResponseDto userDto = adminService.getUserById(userId);

        log.info("Successfully retrieved user with id: {}", userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String authority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id:asc") String sort
    ) {
        log.info("Received request to retrieve users with filters. search={}, isActive={}, role={}",
                search, isActive, authority);

        Sort validatedSort = sortValidator.validateAndBuildSort(sort, UserSortField.class);
        Pageable pageable = PageRequest.of(page, size, validatedSort);
        UserSearchCriteria criteria = new UserSearchCriteria(
                search,
                isActive,
                authority
        );

        Page<UserResponseDto> users = adminService.getAllUsers(pageable, criteria);

        log.info("Successfully retrieved filtered users. Total elements: {}", users.getTotalElements());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody CreateUserRequest createUserRequest,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId) {

        log.info("Received request to create new user with email: {}, requestId: {}",
                createUserRequest.email(), requestId);

        UserResponseDto createdUser = adminService.createUser(createUserRequest);

        log.info("Successfully created user with id: {}, requestId: {}",
                createdUser.userId(), requestId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateUserData(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUpdateUserRequest request
            ) {
        log.info("Received request to update data for user with id: {}", userId);

        UserResponseDto updatedUser = adminService.updateUser(userId, request);

        log.info("Successfully updated data for user with id: {}", userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedUser);
    }

    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateAccountStatus(
            @PathVariable Long userId,
            @Valid @RequestBody AccountStatusRequest request) {

        log.debug("Received request to update account status for user: {}", userId);
        UserResponseDto updatedUser = adminService.updateAccountStatus(userId, request);

        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{userId}/authorities")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateUserAuthorities(
            @PathVariable Long userId,
            @Valid @RequestBody AdminChangeUserAuthorityRequest request) {

        log.debug("Received request to update authorities for user: {}", userId);
        UserResponseDto updatedUser = adminService.updateUserAuthorities(userId, request);

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping({"/{userId}"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserAccount(@PathVariable Long userId) {
        log.info("Received request to delete user with id: {}", userId);

        adminService.deleteUser(userId);

        log.info("Successfully deleted user with id: {}", userId);
        return ResponseEntity.ok().build();
    }


}
