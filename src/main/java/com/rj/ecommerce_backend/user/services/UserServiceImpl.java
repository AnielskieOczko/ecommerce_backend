package com.rj.ecommerce_backend.user.services;

import com.rj.ecommerce_backend.securityconfig.SecurityContextImpl;
import com.rj.ecommerce_backend.securityconfig.dto.AuthResponse;
import com.rj.ecommerce_backend.securityconfig.repositories.RefreshTokenRepository;
import com.rj.ecommerce_backend.securityconfig.services.AuthService;
import com.rj.ecommerce_backend.user.domain.User;
import com.rj.ecommerce_backend.user.dtos.*;
import com.rj.ecommerce_backend.user.exceptions.UserNotFoundException;
import com.rj.ecommerce_backend.user.mappers.UserMapper;
import com.rj.ecommerce_backend.user.repositories.UserRepository;
import com.rj.ecommerce_backend.user.valueobject.Email;
import com.rj.ecommerce_backend.user.valueobject.Password;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND = "User not found for id: ";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final SecurityContextImpl securityContext;
    private final AuthService authService;
    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public UserResponseDto getProfile(Long userId) {
        log.info("Getting profile data for user: {}", userId);
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));
        securityContext.checkAccess(userId);

        log.info("Successfully retrieved profile data for user: {}", userId);
        return userMapper.mapToUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateBasicDetails(Long userId, UpdateBasicDetailsRequest request) {
        log.info("Updating basic details for user: {}", userId);
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));

        securityContext.checkAccess(userId);
        userMapper.updateBasicInformation(user, request);

        User savedUser = userRepository.save(user);
        log.info("Successfully updated basic details for user: {}", userId);
        return userMapper.mapToUserResponseDto(savedUser);
    }

    @Override
    public AuthResponse changeEmail(Long userId, ChangeEmailRequest changeEmailRequest,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        log.info("Processing email change request for user: {}", userId);
        securityContext.checkAccess(userId);
        try {
            User user = userRepository.findUserById(userId)
                    .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));

            // Update email
            String oldEmail = user.getEmail().value();
            user.setEmail(new Email(changeEmailRequest.newEmail()));
            userRepository.save(user);

            log.info("Email changed from {} to {} for user {}",
                    oldEmail, changeEmailRequest.newEmail(), userId);

            return authService.handleEmailUpdate(
                    user,
                    changeEmailRequest.currentPassword(),
                    request, response);
        } catch (Exception e) {
            log.error("Failed to change email for user {}", userId, e);
            return AuthResponse.builder()
                    .success(false)
                    .message("Failed to change email: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        log.info("Updating password for user: {}", userId);

        securityContext.checkAccess(userId);

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));

        if (!request.newPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(request.newPassword());
            user.setPassword(new Password(encodedPassword));
        }

        userRepository.save(user);
        log.info("Successfully updated password for user: {}", userId);
    }

    @Override
    public UserResponseDto updateAccountStatus(Long userId, AccountStatusRequest request) {
        log.debug("Updating account status for user ID: {} to {}", userId, request.active());

        securityContext.checkAccess(userId);

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));

        user.setActive(request.active());
        User savedUser = userRepository.save(user);

        log.info("Successfully updated account status for user ID: {} to {}",
                userId, request.active() ? "active" : "inactive");

        return userMapper.mapToUserResponseDto(savedUser);
    }

    @Override
    public void requestPasswordReset(String email) {
        // TODO: impl later
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        // TODO: impl later
    }

    @Override
    public void deleteAccount(Long userId) {
        log.debug("Deleting account for user with id: {}", userId);
        // securityCheck
        securityContext.checkAccess(userId);

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userId));

        // remove refresh tokens before user account delete
        refreshTokenRepository.deleteByUserId(user.getId());

        userRepository.delete(user);
        log.info("Successfully deleted account for user with id: {}", userId);
    }

}
