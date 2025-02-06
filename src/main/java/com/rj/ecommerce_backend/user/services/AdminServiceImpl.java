package com.rj.ecommerce_backend.user.services;

import com.rj.ecommerce_backend.securityconfig.SecurityContextImpl;
import com.rj.ecommerce_backend.user.domain.Authority;
import com.rj.ecommerce_backend.user.domain.User;
import com.rj.ecommerce_backend.user.dtos.*;
import com.rj.ecommerce_backend.user.exceptions.AuthorityNotFoundException;
import com.rj.ecommerce_backend.user.exceptions.InvalidAuthorityUpdateException;
import com.rj.ecommerce_backend.user.exceptions.UserNotFoundException;
import com.rj.ecommerce_backend.user.mappers.UserMapper;
import com.rj.ecommerce_backend.user.repositories.AuthorityRepository;
import com.rj.ecommerce_backend.user.repositories.UserRepository;
import com.rj.ecommerce_backend.user.valueobject.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final SecurityContextImpl securityContext;


    @Override
    public Optional<User> getUserForValidation(Long userId) {
        securityContext.checkAccess(userId);
        return Optional.ofNullable(userRepository.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    }

    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable, UserSearchCriteria criteria) {
        log.info("Retrieving users with search criteria: {}", criteria);
        securityContext.checkAccess(securityContext.getCurrentUser().getId());

        Specification<User> spec = criteria.toSpecification();

        Page<User> users = userRepository.findAll(spec, pageable);
        return users.map(userMapper::mapToUserResponseDto);
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        securityContext.checkAccess(securityContext.getCurrentUser().getId());
        return userMapper.mapToUserResponseDto(userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId))) ;
    }

    @Override
    public UserResponseDto createUser(CreateUserRequest createUserRequest) {

        Set<String> authorityNames = createUserRequest.authorities();

        Set<Authority> authorities = authorityRepository.findByNameIn(authorityNames);

        if (authorities.size() != authorityNames.size()) {
            throw new AuthorityNotFoundException("One or more authorities not found");
        }

        User user = new User();
        user.setFirstName(createUserRequest.firstName());
        user.setLastName(createUserRequest.lastName());
        user.setEmail(Email.of(createUserRequest.email()));
        user.setAddress(new Address(
                createUserRequest.address().street(),
                createUserRequest.address().city(),
                new ZipCode(createUserRequest.address().zipCode()),
                createUserRequest.address().country()
        ));
        user.setPhoneNumber(new PhoneNumber(createUserRequest.phoneNumber().value()));
        user.setActive(true);
        user.setDateOfBirth(createUserRequest.dateOfBirth());

        // 4. Hash the Password
        String encodedPassword = passwordEncoder.encode(createUserRequest.password());
        user.setPassword(new Password(encodedPassword));

        user.setAuthorities(authorities);

        // 5. Save the User
        User createdUser = userRepository.save(user);

        return userMapper.mapToUserResponseDto(createdUser);

    }

    @Override
    public UserResponseDto updateUser(Long userId, AdminUpdateUserRequest request) {
        log.debug("Updating user with ifrd: {}", userId);

        Objects.requireNonNull(userId, "User ID cannot be null");
        Objects.requireNonNull(request, "Update request cannot be null");

        Long currentUserId = securityContext.getCurrentUser().getId();
        securityContext.checkAccess(currentUserId);
        log.debug("Access verified for admin user: {}", currentUserId);

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId));

        // If authorities are being updated, fetch them from repository
        if (request.authorities() != null && !request.authorities().isEmpty()) {
            Set<Authority> authorities = authorityRepository.findByNameIn(request.authorities());
            if (authorities.size() != request.authorities().size()) {
                throw new AuthorityNotFoundException("One or more authorities not found");
            }
            user.setAuthorities(authorities);
        }

        userMapper.adminUpdateUserInformation(user, request);
        User savedUser = userRepository.save(user);

        log.info("Successfully updated user with id: {}", userId);
        return userMapper.mapToUserResponseDto(savedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        log.debug("Deleting user for id: {}", userId);
        // securityCheck
        securityContext.checkAccess(securityContext.getCurrentUser().getId());

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId));

        userRepository.delete(user);
        log.info("Successfully deleted user with id: {}", userId);
    }

    @Override
    public UserResponseDto updateAccountStatus(Long userId, AccountStatusRequest request) {
        log.debug("Updating account status for user ID: {} to {}", userId, request.active());

        securityContext.checkAccess(securityContext.getCurrentUser().getId());

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        user.setActive(request.active());
        User savedUser = userRepository.save(user);

        log.info("Successfully updated account status for user ID: {} to {}",
                userId, request.active() ? "active" : "inactive");

        return userMapper.mapToUserResponseDto(savedUser);
    }

    @Override
    public UserResponseDto updateUserAuthorities(Long userId, AdminChangeUserAuthorityRequest request) {
        log.debug("Updating authorities for user ID: {}", userId);

        // Security check
        securityContext.checkAccess(securityContext.getCurrentUser().getId());

        // Fetch user
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + userId));

        // Fetch all requested authorities
        Set<Authority> requestedAuthorities = authorityRepository.findByNameIn(request.authorities());
        if (requestedAuthorities.size() != request.authorities().size()) {
            throw new AuthorityNotFoundException("One or more authorities not found");
        }

        // Validate request
        if (requestedAuthorities.isEmpty()) {
            throw new InvalidAuthorityUpdateException("At least one authority must be specified");
        }

        // Update authorities
        user.setAuthorities(requestedAuthorities);
        User savedUser = userRepository.save(user);

        log.info("Successfully updated authorities for user ID: {}. New authority count: {}",
                userId, requestedAuthorities.size());

        return userMapper.mapToUserResponseDto(savedUser);
    }

    @Override
    public void enableUsers(List<Long> userIds) {
        // TODO: to be impl
    }

    @Override
    public void disableUsers(List<Long> userIds) {
        // TODO: to be impl
    }

    @Override
    public void deleteUsers(List<Long> userIds) {
        // TODO: to be impl
    }

    @Override
    public UserStatisticsDTO getUserStatistics() {
        return null;
    }

}
