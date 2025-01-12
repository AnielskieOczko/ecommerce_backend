package com.rj.ecommerce_backend.domain.user.services;

import com.rj.ecommerce_backend.domain.user.Authority;
import com.rj.ecommerce_backend.domain.user.dtos.AuthorityDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AuthorityService {

    void addNewAuthority(Authority authority);

    Set<AuthorityDto> getAllAuthorities();
    Set<String> getAuthorityNames();

    Optional<Authority> findAuthorityByRoleName(String roleName);
}
