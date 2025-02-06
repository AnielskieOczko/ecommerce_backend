package com.rj.ecommerce_backend.user.services;

import com.rj.ecommerce_backend.securityconfig.dto.AuthResponse;
import com.rj.ecommerce_backend.user.dtos.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

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
