package com.rj.ecommerce_backend.domain.user;

import com.rj.ecommerce_backend.domain.user.dtos.CreateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UpdateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.UserResponseDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserControllerImpl implements UserController {

    private final UserService userService;


    @Override
    public List<UserResponseDto> getAllUsers() {
        log.info("Getting list of users");
        return List.of();
    }

    @PostMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        log.info("Getting user with id {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(id));
    }

    @PostMapping("/add")
    @Override
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        log.info("Creating user {}", createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(createUserRequest));
    }

    @PutMapping({"/update/{id}"})
    @Override
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        log.info("Updating user with id {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(id, updateUserRequest));
    }

    @DeleteMapping({"/delete/{id}"})
    @Override
    public void deleteUser(@PathVariable Long id) {
        log.info("Removing user with id {}", id);
        userService.deleteUser(id);
    }
}
