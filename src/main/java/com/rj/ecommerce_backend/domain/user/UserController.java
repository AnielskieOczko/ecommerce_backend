package com.rj.ecommerce_backend.domain.user;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping()
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/add")
    public String addUser() {
        User user = new User();
        user.setName("admin");
        user.setEmail("admin@gmail.com");
        user.setPassword("admin");
        userService.saveUser(user, Set.of("ROLE_USER"));
        log.info(SecurityContextHolder.getContext().getAuthentication().getName());
        return "admin";

    }
}
