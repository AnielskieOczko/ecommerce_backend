package com.rj.ecommerce_backend.domain.user.services;

import com.rj.ecommerce_backend.domain.user.Authority;

import java.util.List;
import java.util.Optional;

public interface AuthorityService {

    void addNewAuthority(Authority authority);

    List<Authority> getAllAuthorities();

    Optional<Authority> findAuthorityByRoleName(String roleName);
}
