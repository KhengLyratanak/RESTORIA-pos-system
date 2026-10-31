package com.nak.demo.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(unique = true, nullable = false, length = 30)
    private String name;
    private Integer age;
    private String address;
    private String role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime  updatedAt;

    @PreUpdate
    public void preUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String normalised = this.role == null || this.role.isBlank() ? "USER" : this.role.trim().toUpperCase();
        return List.of(new SimpleGrantedAuthority("ROLE_" + normalised),
                new SimpleGrantedAuthority("ROLE_" + normalised.toLowerCase()));
    }

    @Override
    public String getUsername() {
        return this.name;
    }
}
