package com.rj.ecommerce_backend.domain.user;

import com.rj.ecommerce_backend.domain.user.valueobject.Address;
import com.rj.ecommerce_backend.domain.user.valueobject.Email;
import com.rj.ecommerce_backend.domain.user.valueobject.Password;
import com.rj.ecommerce_backend.domain.user.valueobject.PhoneNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Embedded
    @NotEmpty
    @AttributeOverride(name = "value", column = @Column(unique = true))
    private Email email;

    @Embedded
    private Password password;

    @Embedded
    private Address address;

    @Embedded
    private PhoneNumber phoneNumber;

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_authorities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private Set<Authority> authorities = new HashSet<>();

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

}

