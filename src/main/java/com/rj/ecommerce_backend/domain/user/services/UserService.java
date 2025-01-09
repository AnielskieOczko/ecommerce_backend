package com.rj.ecommerce_backend.domain.user.services;

import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.dtos.*;
import com.rj.ecommerce_backend.securityconfig.dto.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    // User profile management
    UserResponseDto getProfile(Long userId);
    UserResponseDto updateBasicDetails(Long userId, UpdateBasicDetailsRequest request);
    AuthResponse changeEmail(Long userId, ChangeEmailRequest changeEmailRequest,
                                    HttpServletRequest request,
                                    HttpServletResponse response);
    void changePassword(Long userId, ChangePasswordRequest request);

    // Account management
    UserResponseDto updateAccountStatus(Long userId, AccountStatusRequest request);
    void requestPasswordReset(String email);
    void resetPassword(String token, String newPassword);
    void deleteAccount(Long userId);
}
