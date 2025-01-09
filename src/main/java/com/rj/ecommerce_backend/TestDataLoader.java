package com.rj.ecommerce_backend;

import com.rj.ecommerce_backend.domain.user.Authority;
import com.rj.ecommerce_backend.domain.user.services.AdminService;
import com.rj.ecommerce_backend.domain.user.services.AdminServiceImpl;
import com.rj.ecommerce_backend.domain.user.services.AuthorityServiceImpl;
import com.rj.ecommerce_backend.domain.user.services.UserService;
import com.rj.ecommerce_backend.domain.user.dtos.AddressDto;
import com.rj.ecommerce_backend.domain.user.dtos.CreateUserRequest;
import com.rj.ecommerce_backend.domain.user.dtos.PhoneNumberDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
@Slf4j
@AllArgsConstructor
public class TestDataLoader {

    private final UserService userServiceImpl;
    private final AdminServiceImpl adminService;
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

        AddressDto addressDto = new AddressDto(
                "new street",
                "Wroclaw",
                "55-200",
                "Poland"
        );

        PhoneNumberDto phoneNumberDto = new PhoneNumberDto("777-777-777");

        CreateUserRequest createUserRequest = new CreateUserRequest(
                "firstName",
                "lastName",
                "root@gmail.com",
                "root",
                addressDto,
                phoneNumberDto,
                LocalDate.now(),
                Set.of(ROLE_ADMIN)
        );

        adminService.createUser(createUserRequest);

    }
}
