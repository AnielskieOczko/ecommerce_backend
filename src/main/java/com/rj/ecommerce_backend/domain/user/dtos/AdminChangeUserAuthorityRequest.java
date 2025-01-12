package com.rj.ecommerce_backend.domain.user.dtos;

import java.util.Set;

public record AdminChangeUserAuthorityRequest(Set<String> authorities) {
}
