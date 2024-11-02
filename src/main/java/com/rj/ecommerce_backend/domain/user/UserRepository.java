package com.rj.ecommerce_backend.domain.user;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {



    @Transactional
    Optional<User> findUserByEmail(String email);

    @Transactional
    Optional<User> findUserByName(String name);

    @Transactional
    @Query("SELECT u FROM User u JOIN u.authorities a WHERE a.name = :roleName ORDER BY u.email ASC")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);


}
