package com.rj.ecommerce_backend.domain.user.services;

import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.dtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AdminService {

    // User management
    public Page<UserResponseDto> getAllUsers(Pageable pageable, UserSearchCriteria criteria);
    UserResponseDto getUserById(Long userId);
    UserResponseDto createUser(CreateUserRequest request);
    UserResponseDto updateUser(Long userId, AdminUpdateUserRequest request);
    void deleteUser(Long userId);
    Optional<User> getUserForValidation(Long userId);

    // User status management
    UserResponseDto updateAccountStatus(Long userId, AccountStatusRequest request);

    // Role management
    UserResponseDto updateUserAuthorities(Long userId, AdminChangeUserAuthorityRequest adminChangeUserRoleRequest);

    // Bulk operations
    void enableUsers(List<Long> userIds);
    void disableUsers(List<Long> userIds);
    void deleteUsers(List<Long> userIds);

    // Statistics
    UserStatisticsDTO getUserStatistics();
}
