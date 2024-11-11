package com.rj.ecommerce_backend.domain.user;

import com.rj.ecommerce_backend.domain.user.dtos.CreateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UpdateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UserResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserController {

    List<UserResponseDto> getAllUsers();
    ResponseEntity<UserResponseDto> getUserById(Long id);
    ResponseEntity<UserResponseDto> createUser(CreateUserRequest createUserRequest);
    ResponseEntity<UserResponseDto> updateUser(Long id, UpdateUserRequest updateUserRequest);
    void deleteUser(Long id);
}
