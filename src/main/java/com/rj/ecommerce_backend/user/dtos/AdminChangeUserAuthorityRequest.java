package com.rj.ecommerce_backend.user.dtos;

import java.util.Set;

public record AdminChangeUserAuthorityRequest(Set<String> authorities) {
}
