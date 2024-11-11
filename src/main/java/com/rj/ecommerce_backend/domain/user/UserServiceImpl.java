package com.rj.ecommerce_backend.domain.user;

import com.rj.ecommerce_backend.domain.user.dtos.*;
import com.rj.ecommerce_backend.domain.user.valueobject.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
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
    public UserResponseDto updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setFirstName(updateUserRequest.firstName());
        user.setLastName(updateUserRequest.lastName());
        user.setAddress(new Address(
                updateUserRequest.address().street(),
                updateUserRequest.address().city(),
                new ZipCode(updateUserRequest.address().zipCode()),
                updateUserRequest.address().country()
        ));
        user.setPhoneNumber(new PhoneNumber(updateUserRequest.phoneNumber().value()));
        user.setActive(true);
        user.setDateOfBirth(updateUserRequest.dateOfBirth());

        if (!updateUserRequest.email().isEmpty()) {
            user.setEmail(Email.of(updateUserRequest.email()));
        }

        // 4. Hash the Password
        if (!updateUserRequest.password().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(updateUserRequest.password());
            user.setPassword(new Password(encodedPassword));
        }

        userRepository.save(user);

        return mapToUserResponseDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        log.info("Deleting user with ID: {}", userId);
        userRepository.delete(user);
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

}
