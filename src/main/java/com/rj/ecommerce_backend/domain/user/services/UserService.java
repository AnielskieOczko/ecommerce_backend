package com.rj.ecommerce_backend.domain.user.services;

import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.dtos.CreateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UpdateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    Optional<User> findUserByName(String name);
    Optional<User> findUserByEmail(String email);

    UserResponseDto getUser(Long userId);
    UserResponseDto createUser(CreateUserRequest createUserRequest);
    UserResponseDto updateUser(Long userId, UpdateUserRequest updateUserRequest,
                               HttpServletRequest request,
                               HttpServletResponse response);
    void deleteUser(Long userId);
}
