package com.rj.ecommerce_backend.domain.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    public void addNewAuthority(Authority authority) {
        authorityRepository.save(authority);
    }

    public List<Authority> getAllAuthorities() {
        return authorityRepository.findAll();
    }

    public Optional<Authority> findAuthorityByRoleName(String roleName) {
        return authorityRepository.findByName(roleName);
    }
}
