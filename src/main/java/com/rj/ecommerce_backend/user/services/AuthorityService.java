package com.rj.ecommerce_backend.user.services;

import com.rj.ecommerce_backend.user.domain.Authority;
import com.rj.ecommerce_backend.user.dtos.AuthorityDto;

import java.util.Optional;
import java.util.Set;

public interface AuthorityService {

    void addNewAuthority(Authority authority);

    Set<AuthorityDto> getAllAuthorities();
    Set<String> getAuthorityNames();

    Optional<Authority> findAuthorityByRoleName(String roleName);
}
