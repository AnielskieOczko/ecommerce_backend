package com.rj.ecommerce_backend.domain.user.repositories;

import com.rj.ecommerce_backend.domain.user.User;
import com.rj.ecommerce_backend.domain.user.valueobject.Email;
import jakarta.transaction.Transactional;
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
    Optional<User> findUserByEmail(Email email);

    @Transactional
    Optional<User> findUserByFirstName(String firstName);

    @Transactional
    Optional<User> findUserById(Long userId);

    @Transactional
    @Query("SELECT u FROM User u JOIN u.authorities a WHERE a.name = :roleName ORDER BY u.email ASC")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

    @Transactional
    boolean existsByEmail(Email email);
}
