package com.rj.ecommerce_backend.domain.user;

import com.rj.ecommerce_backend.domain.user.dtos.CreateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UpdateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UserResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    List<User> findAllUsers();
    Optional<User> findUserByName(String name);
    Optional<User> findUserByEmail(String email);

    UserResponseDto getUser(Long userId);
    UserResponseDto createUser(CreateUserRequest createUserRequest);
    UserResponseDto updateUser(Long userId, UpdateUserRequest updateUserRequest);
    void deleteUser(Long userId);
}
