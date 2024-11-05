package com.rj.ecommerce_backend.domain.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test-user") // Added a more specific base path
public class TestUserController {

    @GetMapping("/email") // Added a more specific endpoint
    public ResponseEntity<Map<String, String>> getLoggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {  // Check authentication
            String email = authentication.getName();  // Get the username (which should be the email)
            Map<String, String> response = new HashMap<>();
            response.put("email", email);
            return ResponseEntity.ok(response); // 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Return appropriate error code
        }
    }
}
