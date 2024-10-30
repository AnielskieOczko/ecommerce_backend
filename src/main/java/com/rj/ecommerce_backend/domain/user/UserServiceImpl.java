package com.rj.ecommerce_backend.domain.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> findUserByName(String name) {
        return userRepository.findUserByUsername(name);
    }

    @Override
    public void saveUser(User user) {

        Role userRole = roleRepository.findByName("ROLE_USER");

        HashSet<Role> roles = new HashSet<>();
        roles.add(userRole);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(1);

        user.setRoles(roles);
        userRepository.save(user);

    }
}
