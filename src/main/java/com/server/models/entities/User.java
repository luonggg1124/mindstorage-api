package com.server.models.entities;

import com.server.models.enums.UserProviderName;
import com.server.models.extend.Timestamp;



import com.fasterxml.jackson.annotation.JsonIgnore;
import com.server.models.enums.UserGender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = { "username", "email" }))
public class User extends Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = true)
    @JsonIgnore
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "avatar_url", nullable = true)
    private String avatarUrl;

    @Column(name = "provider_id", nullable = true)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_name", nullable = true)
    private UserProviderName providerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = true)
    private UserGender gender;

    @Column(name = "hobbies", nullable = true)
    private String hobbies;

    @Column(name = "intended_use", nullable = true)
    @JsonIgnore
    private String intendedUse;

}
