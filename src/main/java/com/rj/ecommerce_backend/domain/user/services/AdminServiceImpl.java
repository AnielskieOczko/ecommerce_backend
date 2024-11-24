package com.rj.ecommerce_backend.domain.user.services;

import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.repositories.UserRepository;
import com.rj.ecommerce_backend.domain.user.dtos.CreateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UpdateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;

    @Override
    public List<User> findAllUsers() {
        return List.of();
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public UserResponseDto getUserProfileById(Long userId) {
        return null;
    }

    @Override
    public UserResponseDto createUser(CreateUserRequest createUserRequest) {
        return null;
    }

    @Override
    public UserResponseDto updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        return null;
    }

    @Override
    public void deleteUser(Long userId) {

    }
}
