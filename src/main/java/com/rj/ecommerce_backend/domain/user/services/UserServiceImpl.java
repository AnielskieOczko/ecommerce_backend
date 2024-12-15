package com.rj.ecommerce_backend.domain.user.services;

import com.rj.ecommerce_backend.domain.user.Authority;
import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.dtos.*;
import com.rj.ecommerce_backend.domain.user.exceptions.AuthorityNotFoundException;
import com.rj.ecommerce_backend.domain.user.repositories.AuthorityRepository;
import com.rj.ecommerce_backend.domain.user.repositories.UserRepository;
import com.rj.ecommerce_backend.domain.user.valueobject.*;
import com.rj.ecommerce_backend.securityconfig.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class UserServiceImpl extends SecuredBaseService implements UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogoutService logoutService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(SecurityContextImpl securityContext,
                           UserRepository userRepository,
                           AuthorityRepository authorityRepository,
                           PasswordEncoder passwordEncoder,
                           LogoutService logoutService,
                           JwtUtils jwtUtils,
                           AuthenticationManager authenticationManager) {
        super(securityContext);
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.logoutService = logoutService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }


    @Override
    public Optional<User> findUserByName(String firstname) {
        return userRepository.findUserByFirstName(firstname);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public UserResponseDto getUser(Long userId) {
        return mapToUserResponseDto(userRepository.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    }

    @Override
    public Optional<User> getUserForValidation(Long userId) {
        return Optional.ofNullable(userRepository.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
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
        return mapToUserResponseDto(createdUser);

    }

    @Override
    public UserResponseDto updateUser(Long userId, UpdateUserRequest updateUserRequest,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {

        // 1. Security Check
        checkAccess(userId);

        // 2. Find User
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 3. Store old email for comparison
        String oldEmail = user.getEmail().value();

        // 4. Update basic information
        updateBasicInformation(user, updateUserRequest);

        // 5. Handle email update
        boolean emailChanged = false;
        if (!updateUserRequest.email().isEmpty() && !oldEmail.equals(updateUserRequest.email())) {
            validateNewEmail(updateUserRequest.email());
            user.setEmail(Email.of(updateUserRequest.email()));
            emailChanged = true;
        }

        // 6. Handle password update
        if (!updateUserRequest.password().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updateUserRequest.password());
            user.setPassword(new Password(encodedPassword));
        }

        // 7. Save user
        User savedUser = userRepository.save(user);

        // 8. Handle security context update if email changed
        if (emailChanged) {
            handleEmailUpdate(request, response, savedUser, updateUserRequest.password());
        }

        return mapToUserResponseDto(savedUser);

    }

    @Override
    public void deleteUser(Long userId) {

        // securityCheck
        checkAccess(userId);

        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        log.info("Deleting user with ID: {}", userId);
        userRepository.delete(user);
    }

    private void updateBasicInformation(User user, UpdateUserRequest request) {
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setAddress(new Address(
                request.address().street(),
                request.address().city(),
                new ZipCode(request.address().zipCode()),
                request.address().country()
        ));
        user.setPhoneNumber(new PhoneNumber(request.phoneNumber().value()));
        user.setActive(true);
        user.setDateOfBirth(request.dateOfBirth());
    }

    private UserResponseDto mapToUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail().value(),
                mapToAddressDto(user.getAddress()),
                mapToPhoneNumberDto(user.getPhoneNumber().value()),
                user.getDateOfBirth(),
                user.getAuthorities().stream()
                        .map(Authority::getName)
                        .collect(Collectors.toSet())
        );
    }


    private AddressDto mapToAddressDto(Address address) {
        return new AddressDto(
                address.street(),
                address.city(),
                address.zipCode().value(),
                address.country()
        );
    }

    private PhoneNumberDto mapToPhoneNumberDto(String phoneNumber) {
        return new PhoneNumberDto(
                phoneNumber
        );
    }

    private void validateNewEmail(String newEmail) {
        if (userRepository.existsByEmail(Email.of(newEmail))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }

    private void handleEmailUpdate(HttpServletRequest request,
                                   HttpServletResponse response,
                                   User user,
                                   String password) {
        // 1. Logout the user from the current session
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logoutService.logout(request, response, auth);
        }

        // 2. If password was not changed, we need to get it from the user object
        String credentials = !password.isEmpty() ? password : user.getPassword().value();

        // 3. Create new authentication token with updated email
        Authentication newAuth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail().value(), credentials)
        );

        // 4. Update security context
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // 5. Generate new JWT token
        String newToken = jwtUtils.generateJwtToken(newAuth);

        // 6. Add new token to response header
        response.setHeader("Authorization", "Bearer " + newToken);
    }

}
