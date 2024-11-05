package com.rj.ecommerce_backend;

import com.rj.ecommerce_backend.domain.user.Authority;
import com.rj.ecommerce_backend.domain.user.AuthorityServiceImpl;
import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
@AllArgsConstructor
public class TestDataLoader {

    private final UserService userServiceImpl;
    private final AuthorityServiceImpl authorityServiceImpl;
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationStartUp() {

        log.info("Load test authorities");
        loadAuthorities();

        log.info("Load test users");
        loadUsers();
    }

    @Transactional
    private void loadAuthorities() {
        Authority authority = new Authority();
        authority.setName(ROLE_USER);

        Authority authority1 = new Authority();
        authority1.setName(ROLE_ADMIN);

        authorityServiceImpl.addNewAuthority(authority);
        authorityServiceImpl.addNewAuthority(authority1);
    }

    @Transactional
    private void loadUsers() {

        User user = new User();
        user.setEmail("root@gmail.com");
        user.setPassword("root");

        User user1 = new User();
        user1.setEmail("testUser1@gmail.com");
        user1.setPassword("password1");

        User user2 = new User();
        user2.setEmail("testUser2@gmail.com");
        user2.setPassword("password2");

        Set<String> authorities = Set.of(ROLE_ADMIN);

//        Authority adminAuthority = authorityServiceImpl.findAuthorityByRoleName("ROLE_ADMIN")
//                .orElseThrow(() -> new RuntimeException("Authority not found"));
//
//        user.getAuthorities().add(adminAuthority);

        userServiceImpl.saveUser(user, authorities);

    }

}
