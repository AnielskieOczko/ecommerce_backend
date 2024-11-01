package com.rj.ecommerce_backend.domain.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findUserByName(String name) {
        return userRepository.findUserByName(name);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }


    public User saveUser(User user, Set<String> roleNames) {
        Set<Authority> authorities = authorityRepository.findByNameIn(roleNames);
        if (authorities.size() != roleNames.size()) {
            throw new IllegalArgumentException("One or more authorities not found");
        }
        user.setAuthorities(authorities);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
