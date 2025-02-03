package com.rj.ecommerce_backend.securityconfig.domain;

import com.rj.ecommerce_backend.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    // Add useful audit fields
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "created_by_ip")
    private String createdByIp;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }



}
