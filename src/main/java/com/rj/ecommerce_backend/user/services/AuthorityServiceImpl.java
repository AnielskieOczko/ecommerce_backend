package com.rj.ecommerce_backend.user.services;

import com.rj.ecommerce_backend.user.domain.Authority;
import com.rj.ecommerce_backend.user.dtos.AuthorityDto;
import com.rj.ecommerce_backend.user.repositories.AuthorityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    public void addNewAuthority(Authority authority) {
        authorityRepository.save(authority);
    }

    public Set<AuthorityDto> getAllAuthorities() {

        List<Authority> authorities = authorityRepository.findAll();

        return authorities.stream()
                .map(authority -> new AuthorityDto(authority.getId(),
                        authority.getName()))
                .collect(Collectors.toSet());
    }

    public Set<String> getAuthorityNames() {

        List<Authority> authorities = authorityRepository.findAll();

        return authorities.stream()
                .map(Authority::getName)
                .collect(Collectors.toSet());
    }



    public Optional<Authority> findAuthorityByRoleName(String roleName) {
        return authorityRepository.findByName(roleName);
    }
}
