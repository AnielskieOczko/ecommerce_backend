package com.rj.ecommerce_backend.domain.user;

import com.rj.ecommerce_backend.domain.user.dtos.CreateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UpdateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UserResponseDto;

import java.util.List;
import java.util.Optional;

public interface AdminService {

    List<User> findAllUsers();
    Optional<User> findUserByEmail(String email);
    UserResponseDto getUserProfileById(Long userId);
    UserResponseDto createUser(CreateUserRequest createUserRequest);
    UserResponseDto updateUser(Long userId, UpdateUserRequest updateUserRequest);
    void deleteUser(Long userId);
}
