package com.rj.ecommerce_backend.domain.user.controllers;

import com.rj.ecommerce_backend.domain.user.dtos.AuthorityDto;
import com.rj.ecommerce_backend.domain.user.services.AuthorityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/admin/authorities")
@RequiredArgsConstructor
public class AdminAuthorityController {

    private final AuthorityService authorityService;


    @GetMapping
    public ResponseEntity<Set<AuthorityDto>> getAllAuthorities() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authorityService.getAllAuthorities());
    }

    @GetMapping("/names")
    public ResponseEntity<Set<String>> getAuthoritiesForFilter() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authorityService.getAuthorityNames());
    }

    @PutMapping("/{authorityId}")
    public ResponseEntity<AuthorityDto> updateAuthority(@RequestParam String newAuthName) {
        // TODO: impl
        return null;
    }

}
