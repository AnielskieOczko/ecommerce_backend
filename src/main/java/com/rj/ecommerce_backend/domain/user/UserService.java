package com.rj.ecommerce_backend.domain.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public interface UserService {

    List<User> findAllUsers();
    Optional<User> findUserByName(String name);
    Optional<User> findUserByEmail(String email);
    User saveUser(User user, Set<String> roleNames);
}
