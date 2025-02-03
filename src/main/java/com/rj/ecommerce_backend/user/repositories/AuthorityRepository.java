package com.rj.ecommerce_backend.user.repositories;

import com.rj.ecommerce_backend.user.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Optional<Authority> findByName(String name);

//    Set<Authority> findByNameIn(Set<Authority> authorities);
    Set<Authority> findByNameIn(Set<String> names);
}
