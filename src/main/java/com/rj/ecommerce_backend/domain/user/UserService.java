package com.rj.ecommerce_backend.domain.user;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    Optional<User> findUserByName(String name);
    void saveUser(User user);
}
